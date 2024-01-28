package com.cool.request.view.main;

import com.cool.request.common.bean.components.DynamicComponent;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.scheduled.DynamicSpringScheduled;
import com.cool.request.common.bean.components.scheduled.SpringScheduled;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.component.http.invoke.InvokeResult;
import com.cool.request.component.http.invoke.ScheduledComponentRequest;
import com.cool.request.component.http.net.RequestManager;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.tool.UserProjectManager;
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
        BottomScheduledUI.InvokeClick {
    private final Project project;
    private final HttpRequestParamPanel httpRequestParamPanel;
    private final BottomScheduledUI bottomScheduledUI;
    private Controller currentSelectController;
    private SpringScheduled springScheduled;
    private final CardLayout cardLayout = new CardLayout();
    private final RequestManager requestManager;
    private final UserProjectManager userProjectManager;

    public MainBottomHTTPInvokeViewPanel(@NotNull Project project) {
        this.project = project;
        this.userProjectManager = this.project.getUserData(CoolRequestConfigConstant.UserProjectManagerKey);
        this.httpRequestParamPanel = new HttpRequestParamPanel(project, this);
        this.requestManager = new RequestManager(httpRequestParamPanel.getRequestParamManager(), project, this.userProjectManager);
        this.bottomScheduledUI = new BottomScheduledUI(this);
        this.setLayout(cardLayout);
        this.add(bottomScheduledUI, BottomScheduledUI.class.getName());
        this.add(httpRequestParamPanel, HttpRequestParamPanel.class.getName());
        switchPage(Panel.CONTROLLER);
        httpRequestParamPanel.setSendRequestClickEvent(e -> {
            requestManager.sendRequest(httpRequestParamPanel.getCurrentController());
        });
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        messageBusConnection.subscribe(CoolRequestIdeaTopic.DELETE_ALL_DATA,
                (CoolRequestIdeaTopic.DeleteAllDataEventListener) requestManager::removeAllData);

        messageBusConnection.subscribe(CoolRequestIdeaTopic.SCHEDULED_CHOOSE_EVENT, (CoolRequestIdeaTopic.ScheduledChooseEventListener) scheduled -> scheduledChoose(scheduled));
        messageBusConnection.subscribe(CoolRequestIdeaTopic.CONTROLLER_CHOOSE_EVENT, (CoolRequestIdeaTopic.ControllerChooseEventListener) controller -> controllerChoose(controller));

        /**
         * 更新数据
         */
        project.getMessageBus().connect().subscribe(CoolRequestIdeaTopic.ADD_SPRING_SCHEDULED_MODEL, (CoolRequestIdeaTopic.SpringScheduledModel) newScheduledList -> {
            if (springScheduled == null) return;
            for (SpringScheduled springScheduled : newScheduledList) {
                if (springScheduled.getId().equalsIgnoreCase(springScheduled.getId())) {
                    scheduledChoose(springScheduled);
                    return;
                }
            }
        });

    }

    public String getSelectRequestMappingId() {
        if (this.currentSelectController != null) return this.currentSelectController.getId();
        return "";
    }

    public boolean canEnabledSendButton(String id) {
        return requestManager.canEnabledSendButton(id);
    }

    @Override
    public void onScheduledInvokeClick() {
        if (!(springScheduled instanceof DynamicComponent)) {
            SwingUtilities.invokeLater(() -> Messages.showErrorDialog(ResourceBundleUtils.getString("request.not.running"), "Tip"));
            return;
        }
        ScheduledComponentRequest.InvokeData invokeData = new ScheduledComponentRequest.InvokeData(((DynamicSpringScheduled) springScheduled).getSpringInnerId());
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Call " + springScheduled.getMethodName()) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                InvokeResult invokeResult = new ScheduledComponentRequest(MainBottomHTTPInvokeViewPanel.this.springScheduled.getServerPort()).requestSync(invokeData);
                if (invokeResult.equals(InvokeResult.FAIL)) {
                    SwingUtilities.invokeLater(() -> Messages.showErrorDialog(ResourceBundleUtils.getString("request.fail"), "Tip"));
                }
            }
        });
    }


    public void controllerChoose(Controller controller) {
        this.currentSelectController = controller;
        if (controller == null) return;
        switchPage(Panel.CONTROLLER);
        httpRequestParamPanel.runLoadControllerInfoOnMain(controller);
    }

    public void scheduledChoose(SpringScheduled scheduled) {
        this.springScheduled = scheduled;
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
