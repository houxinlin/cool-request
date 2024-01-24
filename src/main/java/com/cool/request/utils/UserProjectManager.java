package com.cool.request.utils;

import com.cool.request.Constant;
import com.cool.request.IdeaTopic;
import com.cool.request.bean.RefreshInvokeRequestBody;
import com.cool.request.bean.components.Component;
import com.cool.request.bean.components.controller.Controller;
import com.cool.request.bean.components.controller.DynamicController;
import com.cool.request.bean.components.scheduled.DynamicSpringScheduled;
import com.cool.request.bean.components.scheduled.SpringScheduled;
import com.cool.request.invoke.InvokeResult;
import com.cool.request.invoke.RefreshComponentRequest;
import com.cool.request.model.InvokeReceiveModel;
import com.cool.request.model.ProjectStartupModel;
import com.cool.request.tool.CoolRequest;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class UserProjectManager {
    /**
     * 每个项目可以启动N个SpringBoot实例，但是端口会不一样
     */
    private final List<ProjectStartupModel> springBootApplicationStartupModel = new ArrayList<>();
    private final Map<String, CountDownLatch> waitReceiveThread = new HashMap<>();
    private final Project project;
    private final Map<String, String> dynamicControllerIdMap = new HashMap<>();
    private final Map<String, String> dynamicScheduleIdMap = new HashMap<>();
    private final CoolRequest coolRequest;

    public UserProjectManager(Project project, CoolRequest coolRequest) {
        this.project = project;
        this.coolRequest = coolRequest;
    }

    public void clear() {
    }

    public Project getProject() {
        return project;
    }

    public void refreshComponents() {
        project.putUserData(Constant.ServerMessageRefreshModelSupplierKey, () -> Boolean.TRUE);
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Refresh") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Set<Integer> failPort = new HashSet<>();
                for (ProjectStartupModel projectStartupModel : springBootApplicationStartupModel) {
                    InvokeResult invokeResult = new RefreshComponentRequest(projectStartupModel.getPort()).requestSync(new RefreshInvokeRequestBody());
                    if (invokeResult == InvokeResult.FAIL) failPort.add(projectStartupModel.getProjectPort());
                }
                if (!failPort.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        String ports = failPort.stream().map(String::valueOf)
                                .collect(Collectors.joining("、"));
                        Messages.showErrorDialog(ResourceBundleUtils.getString("unable.refresh") + " " + ports, "Tip");
                    });
                }
            }
        });
    }

    public void addSpringBootApplicationInstance(int projectPort, int startPort) {
        springBootApplicationStartupModel.add(new ProjectStartupModel(projectPort, startPort));
    }

    public void addComponent(List<? extends Component> data) {
        if (data == null || data.isEmpty()) return;
        if (!coolRequest.canAddComponentToView()) {
            coolRequest.addBacklogData((List<Component>) data);
            return;
        }
        if (data.get(0) instanceof Controller) {
            addControllerInfo((List<? extends Controller>) data);
        }
        if (data.get(0) instanceof SpringScheduled) {
            addScheduledInfo((List<? extends SpringScheduled>) data);
        }
    }

    private void addControllerInfo(List<? extends Controller> controllers) {
        for (Controller controller : controllers) {
            if (controller instanceof DynamicController) {
                dynamicControllerIdMap.put(((DynamicController) controller).getSpringInnerId(), controller.getId());
            }
        }
        IdeaTopic.SpringRequestMappingModel springRequestMappingModel = this.project.getMessageBus()
                .syncPublisher(IdeaTopic.ADD_SPRING_REQUEST_MAPPING_MODEL);

        SwingUtilities.invokeLater(() -> {
            springRequestMappingModel.addRequestMappingModel(controllers);
            springRequestMappingModel.restore();
        });
    }

    private void addScheduledInfo(List<? extends SpringScheduled> scheduleds) {
        for (SpringScheduled controller : scheduleds) {
            if (controller instanceof DynamicSpringScheduled) {
                dynamicScheduleIdMap.put(((DynamicSpringScheduled) controller).getSpringInnerId(), controller.getId());
            }
        }

        this.project.getMessageBus()
                .syncPublisher(IdeaTopic.ADD_SPRING_SCHEDULED_MODEL)
                .addSpringScheduledModel(scheduleds);
    }

    public String getDynamicControllerRawId(String springInnerId) {
        return dynamicControllerIdMap.getOrDefault(springInnerId, "");
    }

    public void registerWaitReceive(String id, CountDownLatch countDownLatch) {
        waitReceiveThread.put(id, countDownLatch);
    }

    public void onInvokeReceive(InvokeReceiveModel invokeReceiveModel) {
        CountDownLatch countDownLatch = waitReceiveThread.remove(invokeReceiveModel.getRequestId());
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }

    }
}
