package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.components.DynamicComponent;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.bean.components.scheduled.DynamicSpringScheduled;
import com.hxl.plugin.springboot.invoke.bean.components.scheduled.SpringScheduled;
import com.hxl.plugin.springboot.invoke.invoke.InvokeResult;
import com.hxl.plugin.springboot.invoke.invoke.ScheduledComponentRequest;
import com.hxl.plugin.springboot.invoke.net.RequestManager;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.utils.SpringScheduledSpringInvokeEndpointWrapper;
import com.hxl.plugin.springboot.invoke.utils.UserProjectManager;
import com.hxl.plugin.springboot.invoke.view.BottomScheduledUI;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 负责管理http参数和调度器参数UI的容器
 */
public class MainBottomHTTPInvokeViewPanel extends JPanel implements
        BottomScheduledUI.InvokeClick {
    private final Project project;
    private final CoolIdeaPluginWindowView coolIdeaPluginWindowView;
    private final HttpRequestParamPanel httpRequestParamPanel;
    private final BottomScheduledUI bottomScheduledUI;
    private Controller currentSelectController;
    private SpringScheduled springScheduled;
    private final CardLayout cardLayout = new CardLayout();
    private final RequestManager requestManager;
    private final UserProjectManager userProjectManager;

    public MainBottomHTTPInvokeViewPanel(@NotNull Project project, CoolIdeaPluginWindowView coolIdeaPluginWindowView) {
        this.coolIdeaPluginWindowView = coolIdeaPluginWindowView;
        this.project = project;
        this.userProjectManager = this.project.getUserData(Constant.UserProjectManagerKey);
        this.httpRequestParamPanel = new HttpRequestParamPanel(project, this);
        this.requestManager = new RequestManager(httpRequestParamPanel.getRequestParamManager(), project, this.userProjectManager);
        this.bottomScheduledUI = new BottomScheduledUI(this);
        this.setLayout(cardLayout);
        this.add(bottomScheduledUI, BottomScheduledUI.class.getName());
        this.add(httpRequestParamPanel, HttpRequestParamPanel.class.getName());
        switchPage(Panel.CONTROLLER);
        httpRequestParamPanel.setSendRequestClickEvent(e -> requestManager.sendRequest(httpRequestParamPanel.getCurrentController()));
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        messageBusConnection.subscribe(IdeaTopic.DELETE_ALL_DATA,
                (IdeaTopic.DeleteAllDataEventListener) requestManager::removeAllData);

        messageBusConnection.subscribe(IdeaTopic.SCHEDULED_CHOOSE_EVENT, (IdeaTopic.ScheduledChooseEventListener) scheduled -> scheduledChoose(scheduled));
        messageBusConnection.subscribe(IdeaTopic.CONTROLLER_CHOOSE_EVENT, (IdeaTopic.ControllerChooseEventListener) controller -> controllerChoose(controller));

        /**
         * 更新数据
         */
        project.getMessageBus().connect().subscribe(IdeaTopic.ADD_SPRING_SCHEDULED_MODEL, (IdeaTopic.SpringScheduledModel) newScheduledList -> {
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


    private void controllerChoose(Controller controller) {
        this.currentSelectController = controller;
        if (controller == null) return;
        switchPage(Panel.CONTROLLER);
    }

    private void scheduledChoose(SpringScheduled scheduled) {
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
