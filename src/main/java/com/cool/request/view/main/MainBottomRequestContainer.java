/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * MainBottomRequestContainer.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.main;

import com.cool.request.agent.trace.TraceHTTPListener;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.model.ProjectStartupModel;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.TemporaryController;
import com.cool.request.components.http.net.RequestContext;
import com.cool.request.components.http.net.RequestManager;
import com.cool.request.components.scheduled.BasicScheduled;
import com.cool.request.components.scheduled.XxlJobScheduled;
import com.cool.request.rmi.RMIFactory;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.ResourceBundleUtils;
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
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负责管理http参数和调度器参数UI的容器
 */
public class MainBottomRequestContainer extends JPanel implements
        BottomScheduledUI.InvokeClick, Disposable {
    private final Project project;
    private HttpRequestParamPanel httpRequestParamPanel;
    private BottomScheduledUI bottomScheduledUI;
    private Controller currentSelectController;
    private BasicScheduled basicScheduled;
    private final CardLayout cardLayout = new CardLayout();
    private RequestManager requestManager;
    private final UserProjectManager userProjectManager;
    private Map<String, String> xxlParamMap = new HashMap<>();

    private MainBottomHTTPContainer mainBottomHTTPContainer;

    public MainBottomRequestContainer(@NotNull Project project,
                                      MainBottomHTTPContainer mainBottomHTTPContainer) {
        this.project = project;
        this.mainBottomHTTPContainer = mainBottomHTTPContainer;
        this.userProjectManager = UserProjectManager.getInstance(project);
        this.httpRequestParamPanel = new HttpRequestParamPanel(project, this, mainBottomHTTPContainer);
        this.requestManager = new RequestManager(httpRequestParamPanel.getRequestParamManager(), project, this.userProjectManager);
        this.bottomScheduledUI = new BottomScheduledUI(this);
        this.setLayout(cardLayout);
        this.add(bottomScheduledUI, BottomScheduledUI.class.getName());
        this.add(httpRequestParamPanel, HttpRequestParamPanel.class.getName());
        switchPage(Panel.CONTROLLER);
        httpRequestParamPanel.setSendRequestClickEvent(e -> sendRequest());
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();
        messageBusConnection.subscribe(CoolRequestIdeaTopic.DELETE_ALL_DATA, requestManager::removeAllData);

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
        RequestContext requestContext = new RequestContext(controller, project);
        requestContext.setHttpEventListeners(buildHTTPEventListener());
        return requestContext;
    }

    private List<HTTPEventListener> buildHTTPEventListener() {
        ArrayList<HTTPEventListener> httpEventListeners = new ArrayList<>();
        httpEventListeners.add(mainBottomHTTPContainer.getMainBottomHTTPResponseView());
        httpEventListeners.add(new TraceHTTPListener(project,
                mainBottomHTTPContainer.getMainBottomHTTPResponseView().getTracePreviewView()));
        return httpEventListeners;
    }

    /**
     * 发送请求前处理一些反射调用的逻辑
     */
    private void sendRequest() {
        /**
         * 一般情况下这里都不会为空,只有第一次启动，并且选择HTTP参数界面是为空
         */
        Controller controller = httpRequestParamPanel.getCurrentController();
        if (controller == null) {
            //构建为自定义请求
            controller = httpRequestParamPanel.buildAsCustomController(TemporaryController.class);
        }
        RequestContext requestContext = createRequestContext(controller);

        //临时发起得Controller，需要通知其他组件选中数据
        if (controller instanceof TemporaryController) {
            project.getMessageBus().syncPublisher(CoolRequestIdeaTopic.COMPONENT_CHOOSE_EVENT).onChooseEvent(controller);
        }
        //如果是静态数据，并且是反射请求，则尝试发起动态数据拉取请求
        doSendRequest(requestContext);
    }

    @Override
    public void onScheduledInvokeClick() {
        String param = null;
        if (basicScheduled instanceof XxlJobScheduled) {
            param = Messages.showInputDialog(
                    ResourceBundleUtils.getString("xxl.job.param"),
                    "Tip",
                    CoolRequestIcons.XXL_JOB, xxlParamMap.getOrDefault(basicScheduled.getId(), ""), null);
            xxlParamMap.put(basicScheduled.getId(), param);
        }

        String methodName = basicScheduled.getMethodName();
        String finalParam = param;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Call " + methodName) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                int port = basicScheduled.getServerPort();
                try {
                    if (port <= 0) {
                        List<ProjectStartupModel> springBootApplicationStartupModel =
                                UserProjectManager.getInstance(project).getSpringBootApplicationStartupModel();
                        if (springBootApplicationStartupModel.isEmpty()) {
                            MessagesWrapperUtils.showErrorDialog("Please start the project", "Tip");
                            return;
                        }
                        for (ProjectStartupModel projectStartupModel : springBootApplicationStartupModel) {
                            indicator.setText("Try port on " + projectStartupModel.getProjectPort());
                            try {
                                if (RMIFactory.getStarterRMI(projectStartupModel.getPort())
                                        .invokeScheduled(basicScheduled.getClassName(), basicScheduled.getMethodName(), finalParam)) {
                                    return;
                                }
                            } catch (Exception ignored) {
                            }
                        }
                        return;
                    }
                    RMIFactory.getStarterRMI(port)
                            .invokeScheduled(basicScheduled.getClassName(), basicScheduled.getMethodName(), finalParam);
                } catch (RemoteException e) {
                    MessagesWrapperUtils.showErrorDialog("Call fail", "Tip");
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
