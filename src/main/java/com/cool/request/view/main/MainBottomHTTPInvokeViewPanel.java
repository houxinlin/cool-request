package com.cool.request.view.main;

import com.cool.request.common.bean.components.DynamicComponent;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.DynamicController;
import com.cool.request.common.bean.components.controller.StaticController;
import com.cool.request.common.bean.components.controller.TemporaryController;
import com.cool.request.common.bean.components.scheduled.BasicScheduled;
import com.cool.request.common.bean.components.scheduled.XxlJobScheduled;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.component.http.DynamicDataManager;
import com.cool.request.component.http.invoke.InvokeResult;
import com.cool.request.component.http.invoke.ScheduledComponentRequest;
import com.cool.request.component.http.invoke.body.ReflexScheduledRequestBody;
import com.cool.request.component.http.net.RequestContext;
import com.cool.request.component.http.net.RequestManager;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 负责管理http参数和调度器参数UI的容器
 */
public class MainBottomHTTPInvokeViewPanel extends JPanel implements
        BottomScheduledUI.InvokeClick, Disposable {
    private final Project project;
    private HttpRequestParamPanel httpRequestParamPanel;
    private BottomScheduledUI bottomScheduledUI;
    private Controller currentSelectController;
    private BasicScheduled basicScheduled;
    private final CardLayout cardLayout = new CardLayout();
    private RequestManager requestManager;
    private final UserProjectManager userProjectManager;
    private final HTTPEventManager httpEventManager;

    public MainBottomHTTPInvokeViewPanel(@NotNull Project project,
                                         HTTPEventManager sendEventManager,
                                         MainBottomHTTPContainer mainBottomHTTPContainer) {
        this.project = project;
        this.httpEventManager = sendEventManager;
        this.userProjectManager = this.project.getUserData(CoolRequestConfigConstant.UserProjectManagerKey);
        this.httpRequestParamPanel = new HttpRequestParamPanel(project, this, mainBottomHTTPContainer);
        this.requestManager = new RequestManager(httpRequestParamPanel.getRequestParamManager(), project,
                this.userProjectManager, httpEventManager);
        this.bottomScheduledUI = new BottomScheduledUI(this);
        this.setLayout(cardLayout);
        this.add(bottomScheduledUI, BottomScheduledUI.class.getName());
        this.add(httpRequestParamPanel, HttpRequestParamPanel.class.getName());
        switchPage(Panel.CONTROLLER);
        httpRequestParamPanel.setSendRequestClickEvent(e -> sendRequest());
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        messageBusConnection.subscribe(CoolRequestIdeaTopic.DELETE_ALL_DATA, requestManager::removeAllData);
        sendEventManager.register(httpRequestParamPanel);
        Disposer.register(this, httpRequestParamPanel);
        Disposer.register(this, requestManager);
    }

    @Override
    public void dispose() {
        requestManager = null;
        httpRequestParamPanel = null;
        bottomScheduledUI = null;
    }

    private RequestContext createRequestContext(Controller controller) {
        RequestContext requestContext = new RequestContext(controller);
        requestContext.setHttpEventListeners(buildHTTPEventListener());
        return requestContext;
    }

    private List<HTTPEventListener> buildHTTPEventListener() {
        //一定要返回新的，httpEventManager.getHttpEventListeners()是全局的事件监听器
        return new ArrayList<>(httpEventManager.getHttpEventListeners());
    }

    /**
     * 发送请求前处理一些反射调用的逻辑
     */
    private void sendRequest() {
        Controller controller = httpRequestParamPanel.getCurrentController();
        if (controller == null) {
            controller = httpRequestParamPanel.buildAsCustomController(TemporaryController.class);
        }
        RequestContext requestContext = createRequestContext(controller);

        //临时发起得Controller，需要通知其他组件选中数据
        if (controller instanceof TemporaryController) {
            project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.COMPONENT_CHOOSE_EVENT).onChooseEvent(controller);
        }
        //如果是静态数据，并且是反射请求
        if (controller instanceof StaticController && httpRequestParamPanel.isReflexRequest()) {
            //尝试拉取动态数据
            class PullFailCallback implements Runnable {
                @Override
                public void run() {
                    String msg = ResourceBundleUtils.getString("pull.dynamic.data.fail");
                    MessagesWrapperUtils.showErrorDialog(msg, "Tip");
                    httpEventManager.sendEnd(requestContext, null);
                }
            }
            class PullSuccessCallback implements Consumer<DynamicController> {
                @Override
                public void accept(DynamicController dynamicController) {
                    doSendRequest(createRequestContext(dynamicController));
                }
            }
            requestContext.beginSend(requestContext);
            DynamicDataManager.getInstance(project).pullDynamicData(controller, new PullSuccessCallback(), new PullFailCallback());
            return;
        }
        doSendRequest(requestContext);

    }

    @Override
    public void onScheduledInvokeClick() {
        for (BasicScheduled scheduled : userProjectManager.getScheduled()) {
            if (StringUtils.isEqualsIgnoreCase(scheduled.getId(), this.basicScheduled.getId())) {
                if (scheduled instanceof DynamicComponent) {
                    basicScheduled = scheduled;
                }
            }
        }
        if (!(basicScheduled instanceof DynamicComponent)) {
            SwingUtilities.invokeLater(() -> Messages.showErrorDialog(ResourceBundleUtils.getString("request.not.running"), ResourceBundleUtils.getString("tip")));
            return;
        }
        String springInnerId = ((DynamicComponent) basicScheduled).getSpringInnerId();
        String param = null;
        if (basicScheduled instanceof XxlJobScheduled) {
            param = Messages.showInputDialog(ResourceBundleUtils.getString("xxl.job.param"), "Tip", CoolRequestIcons.XXL_JOB);
        }

        String methodName = "";
        ReflexScheduledRequestBody reflexScheduledRequestBody = new ReflexScheduledRequestBody();
        reflexScheduledRequestBody.setId(springInnerId);
        reflexScheduledRequestBody.setParam(param);
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Call " + methodName) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                int port = basicScheduled.getServerPort();
                InvokeResult invokeResult = new ScheduledComponentRequest(port).requestSync(reflexScheduledRequestBody);
                if (invokeResult.equals(InvokeResult.FAIL)) {
                    SwingUtilities.invokeLater(() -> Messages.showErrorDialog(ResourceBundleUtils.getString("request.fail"), ResourceBundleUtils.getString("tip")));
                }
            }
        });
    }


    public void controllerChoose(Controller controller) {
        this.currentSelectController = controller;
        this.basicScheduled = null;
        if (controller == null) return;
        switchPage(Panel.CONTROLLER);
        httpRequestParamPanel.runLoadControllerInfoOnMain(controller);

    }

    public void scheduledChoose(BasicScheduled scheduled) {
        this.basicScheduled = scheduled;
        this.currentSelectController = null;
        if (scheduled == null) return;
        switchPage(Panel.SCHEDULED);
        bottomScheduledUI.setText("Invoke:" + scheduled.getMethodName() + "()");
    }

    private void doSendRequest(RequestContext requestContext) {
        requestManager.sendRequest(requestContext);
    }


    public boolean canEnabledSendButton(String id) {
        return requestManager.canEnabledSendButton(id);
    }

    public Controller getController() {
        return currentSelectController;
    }

    private void switchPage(Panel panel) {
        cardLayout.show(this, panel == Panel.CONTROLLER ?
                HttpRequestParamPanel.class.getName()
                : BottomScheduledUI.class.getName());
    }

    public void clearRequestParam() {
        this.httpRequestParamPanel.clearAllRequestParam();
    }

    public HttpRequestParamPanel getHttpRequestParamPanel() {
        return httpRequestParamPanel;
    }

    private enum Panel {
        CONTROLLER, SCHEDULED
    }

}
