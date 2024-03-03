package com.cool.request.view.main;

import com.cool.request.common.bean.components.DynamicComponent;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.StaticController;
import com.cool.request.common.bean.components.controller.TemporaryController;
import com.cool.request.common.bean.components.scheduled.BasicScheduled;
import com.cool.request.common.bean.components.scheduled.SpringScheduled;
import com.cool.request.common.bean.components.scheduled.XxlJobScheduled;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.component.http.DynamicDataManager;
import com.cool.request.component.http.invoke.InvokeResult;
import com.cool.request.component.http.invoke.ScheduledComponentRequest;
import com.cool.request.component.http.invoke.body.ReflexScheduledRequestBody;
import com.cool.request.component.http.net.RequestManager;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * 负责管理http参数和调度器参数UI的容器
 */
public class MainBottomHTTPInvokeViewPanel extends JPanel implements
        BottomScheduledUI.InvokeClick , Disposable {
    private final Project project;
    private final HttpRequestParamPanel httpRequestParamPanel;
    private final BottomScheduledUI bottomScheduledUI;
    private Controller currentSelectController;
    private BasicScheduled basicScheduled;
    private final CardLayout cardLayout = new CardLayout();
    private final RequestManager requestManager;
    private final UserProjectManager userProjectManager;

    private HTTPSendEventManager httpSendEventManager;

    public MainBottomHTTPInvokeViewPanel(@NotNull Project project, HTTPSendEventManager sendEventManager) {
        this.project = project;
        this.httpSendEventManager = sendEventManager;
        this.userProjectManager = this.project.getUserData(CoolRequestConfigConstant.UserProjectManagerKey);
        this.httpRequestParamPanel = new HttpRequestParamPanel(project, this);
        this.requestManager = new RequestManager(httpRequestParamPanel.getRequestParamManager(), project,
                this.userProjectManager, httpSendEventManager);
        this.bottomScheduledUI = new BottomScheduledUI(this);
        this.setLayout(cardLayout);
        this.add(bottomScheduledUI, BottomScheduledUI.class.getName());
        this.add(httpRequestParamPanel, HttpRequestParamPanel.class.getName());
        switchPage(Panel.CONTROLLER);
        httpRequestParamPanel.setSendRequestClickEvent(e -> sendRequest());
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        messageBusConnection.subscribe(CoolRequestIdeaTopic.DELETE_ALL_DATA,
                (CoolRequestIdeaTopic.DeleteAllDataEventListener) requestManager::removeAllData);
        sendEventManager.register(httpRequestParamPanel);

        /**
         * 更新数据
         */
        project.getMessageBus().connect().subscribe(CoolRequestIdeaTopic.ADD_SPRING_SCHEDULED_MODEL, (CoolRequestIdeaTopic.SpringScheduledModel) newScheduledList -> {
            if (basicScheduled == null) return;
            for (SpringScheduled springScheduled : newScheduledList) {
                if (springScheduled.getId().equalsIgnoreCase(springScheduled.getId())) {
                    scheduledChoose(springScheduled);
                    return;
                }
            }
        });
    }

    @Override
    public void dispose() {
        httpRequestParamPanel.dispose();
    }

    private void sendRequest() {
        Controller controller = httpRequestParamPanel.getCurrentController();
        if (controller == null) {
            controller = httpRequestParamPanel.buildAsCustomController(TemporaryController.class);
        }
        //临时发起得Controller，需要通知其他组件选中数据
        if (controller instanceof TemporaryController) {
            project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.COMPONENT_CHOOSE_EVENT).onChooseEvent(controller);
        }
        //如果是静态数据，并且是反射请求
        if (controller instanceof StaticController && httpRequestParamPanel.isReflexRequest()) {
            //尝试拉取动态数据
            class PullFailCallback implements Runnable {
                private final Controller controller;

                public PullFailCallback(Controller controller) {
                    this.controller = controller;
                }

                @Override
                public void run() {
                    String msg = ResourceBundleUtils.getString("pull.dynamic.data.fail");
                    MessagesWrapperUtils.showErrorDialog(msg, "Tip");
                    httpSendEventManager.sendEnd(controller);
                }
            }
            httpSendEventManager.sendEnd(controller);
            DynamicDataManager.getInstance(project).pullDynamicData(controller, this::doSendRequest, new PullFailCallback(controller));
            return;
        }
        doSendRequest(controller);

    }

    private void doSendRequest(Controller controller) {
        requestManager.sendRequest(controller);
    }


    public boolean canEnabledSendButton(String id) {
        return requestManager.canEnabledSendButton(id);
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
