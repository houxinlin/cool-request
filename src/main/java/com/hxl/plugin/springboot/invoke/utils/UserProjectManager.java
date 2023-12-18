package com.hxl.plugin.springboot.invoke.utils;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.RefreshInvokeRequestBody;
import com.hxl.plugin.springboot.invoke.invoke.InvokeResult;
import com.hxl.plugin.springboot.invoke.invoke.RefreshInvoke;
import com.hxl.plugin.springboot.invoke.model.*;
import com.hxl.plugin.springboot.invoke.script.ILog;
import com.hxl.plugin.springboot.invoke.script.ScriptSimpleLogImpl;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class UserProjectManager {
    /**
     * 每个项目可以启动N个SpringBoot实例，但是端口会不一样
     */
    private final Map<Integer, ProjectEndpoint> springBootApplicationInstanceData = new HashMap<>();
    private final List<ProjectStartupModel> springBootApplicationStartupModel = new ArrayList<>();
    private final Map<String, CountDownLatch> waitReceiveThread = new HashMap<>();
    private final Project project;
    private final ILog scriptSimpleLog;

    public UserProjectManager(Project project) {
        this.project = project;
        this.scriptSimpleLog = new ScriptSimpleLogImpl(project);
    }

    public void clear() {
//        springBootApplicationStartupModel.clear();
    }

    public ILog getScriptSimpleLog() {
        return scriptSimpleLog;
    }

    public Project getProject() {
        return project;
    }

    public void onUserProjectStartup(ProjectStartupModel model) {
        this.springBootApplicationStartupModel.removeIf(testModel -> testModel.getProjectPort() == model.getProjectPort());
        this.springBootApplicationStartupModel.add(model);

    }

    public void removeIfClosePort() {
        Set<Integer> result = new HashSet<>();
        for (Integer port : springBootApplicationInstanceData.keySet()) {
            try (SocketChannel ignored = SocketChannel.open(new InetSocketAddress(port))) {
            } catch (Exception e) {
                result.add(port);
            }
        }
        result.forEach(springBootApplicationInstanceData::remove);
    }

    public void addControllerInfo(RequestMappingModel requestMappingModel) {
        ProjectEndpoint projectModuleBean = springBootApplicationInstanceData.computeIfAbsent(requestMappingModel.getPort(), integer -> new ProjectEndpoint());
        projectModuleBean.getController().add(requestMappingModel);
        this.project.getMessageBus()
                .syncPublisher(IdeaTopic.ADD_SPRING_REQUEST_MAPPING_MODEL)
                .addRequestMappingModel(requestMappingModel);
    }

    public void addScheduleInfo(ScheduledModel scheduledModel) {
        ProjectEndpoint projectModuleBean = springBootApplicationInstanceData.computeIfAbsent(scheduledModel.getPort(), integer -> new ProjectEndpoint());
        projectModuleBean.getScheduled().addAll(scheduledModel.getScheduledInvokeBeans());
        project.getMessageBus()
                .syncPublisher(IdeaTopic.ADD_SPRING_SCHEDULED_MODEL)
                .addSpringScheduledModel(scheduledModel);

    }

    public void projectEndpointRefresh() {
        if (springBootApplicationStartupModel.isEmpty()) {
            Messages.showErrorDialog("Please start the project", "Tip");
            return;
        }
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Refresh") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Set<Integer> failPort = new HashSet<>();
                for (ProjectStartupModel projectStartupModel : springBootApplicationStartupModel) {
                    InvokeResult invokeResult = new RefreshInvoke(projectStartupModel.getPort()).invokeSync(new RefreshInvokeRequestBody());
                    if (invokeResult == InvokeResult.FAIL) failPort.add(projectStartupModel.getProjectPort());
                }
                if (!failPort.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        String ports = failPort.stream().map(String::valueOf)
                                .collect(Collectors.joining("、"));
                        Messages.showErrorDialog("Unable to refresh on port " + ports, "Tip");
                    });
                }
            }
        });
    }

    public List<ProjectStartupModel> getSpringBootApplicationStartupModel() {
        return springBootApplicationStartupModel;
    }

    public void onInvokeReceive(InvokeReceiveModel invokeResponseModel) {
        CountDownLatch countDownLatch = waitReceiveThread.remove(invokeResponseModel.getRequestId());
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public void registerWaitReceive(String id, CountDownLatch thread) {
        waitReceiveThread.put(id, thread);
    }

    public static class ProjectEndpoint {
        private Set<RequestMappingModel> controller = new HashSet<>();
        private Set<SpringScheduledSpringInvokeEndpoint> scheduled = new HashSet<>();

        public Set<RequestMappingModel> getController() {
            return controller;
        }

        public void setController(Set<RequestMappingModel> controller) {
            this.controller = controller;
        }

        public Set<SpringScheduledSpringInvokeEndpoint> getScheduled() {
            return scheduled;
        }

        public void setScheduled(Set<SpringScheduledSpringInvokeEndpoint> scheduled) {
            this.scheduled = scheduled;
        }
    }
}
