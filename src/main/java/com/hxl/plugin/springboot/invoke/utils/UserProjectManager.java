package com.hxl.plugin.springboot.invoke.utils;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.RefreshInvokeRequestBody;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.bean.components.controller.DynamicController;
import com.hxl.plugin.springboot.invoke.bean.components.scheduled.DynamicSpringScheduled;
import com.hxl.plugin.springboot.invoke.bean.components.scheduled.SpringScheduled;
import com.hxl.plugin.springboot.invoke.invoke.InvokeResult;
import com.hxl.plugin.springboot.invoke.invoke.RefreshComponentRequest;
import com.hxl.plugin.springboot.invoke.model.InvokeReceiveModel;
import com.hxl.plugin.springboot.invoke.model.ProjectStartupModel;
import com.hxl.plugin.springboot.invoke.script.ILog;
import com.hxl.plugin.springboot.invoke.script.ScriptSimpleLogImpl;
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
//    private final Map<Integer, ProjectEndpoint> springBootApplicationInstanceData = new HashMap<>();
    private final List<ProjectStartupModel> springBootApplicationStartupModel = new ArrayList<>();
    private final Map<String, CountDownLatch> waitReceiveThread = new HashMap<>();
    private final Project project;
    private final ILog scriptSimpleLog;
    private final Map<String, String> dynamicControllerIdMap = new HashMap<>();
    private final Map<String, String> dynamicScheduleIdMap = new HashMap<>();
    //
    public UserProjectManager(Project project) {
        this.project = project;
        this.scriptSimpleLog = new ScriptSimpleLogImpl(project);
    }

    public void clear() {
    }

    //    public ILog getScriptSimpleLog() {
//        return scriptSimpleLog;
//    }
//
    public Project getProject() {
        return project;
    }

    public void refreshComponents() {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Refresh") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Set<Integer> failPort = new HashSet<>();
                for (ProjectStartupModel projectStartupModel : springBootApplicationStartupModel) {
                    InvokeResult invokeResult = new RefreshComponentRequest(projectStartupModel.getPort()).requestSync(new RefreshInvokeRequestBody());
                    if (invokeResult == InvokeResult.FAIL) failPort.add(projectStartupModel.getWebPort());
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

    public void addSpringBootApplicationInstance(int webPort, int startPort) {
        springBootApplicationStartupModel.add(new ProjectStartupModel(startPort, webPort));
    }

    //
//    public void removeIfClosePort() {
//        Set<Integer> result = new HashSet<>();
//        for (Integer port : springBootApplicationInstanceData.keySet()) {
//            try (SocketChannel ignored = SocketChannel.open(new InetSocketAddress(port))) {
//            } catch (Exception e) {
//                result.add(port);
//            }
//        }
//        result.forEach(springBootApplicationInstanceData::remove);
//    }
//
    public void addControllerInfo(List<? extends Controller> controllers) {
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

    public void addScheduledInfo(List<?extends SpringScheduled> scheduleds) {
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


//    public void addScheduleInfo(ScheduledModel scheduledModel) {
//        ProjectEndpoint projectModuleBean = springBootApplicationInstanceData.computeIfAbsent(scheduledModel.getPort(), integer -> new ProjectEndpoint());
//        projectModuleBean.getScheduled().addAll(scheduledModel.getScheduledInvokeBeans());
//        project.getMessageBus()
//                .syncPublisher(IdeaTopic.ADD_SPRING_SCHEDULED_MODEL)
//                .addSpringScheduledModel(scheduledModel);
//
//    }
//
//    public void projectEndpointRefresh() {
//        if (springBootApplicationStartupModel.isEmpty()) {
//            Messages.showErrorDialog(ResourceBundleUtils.getString("start.project.tip"), ResourceBundleUtils.getString("tip"));
//            return;
//        }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    }
//
//    public List<ProjectStartupModel> getSpringBootApplicationStartupModel() {
//        return springBootApplicationStartupModel;
//    }
//
//    public void onInvokeReceive(InvokeReceiveModel invokeResponseModel) {
//        CountDownLatch countDownLatch = waitReceiveThread.remove(invokeResponseModel.getRequestId());
//        if (countDownLatch != null) {
//            countDownLatch.countDown();
//        }
//    }
//
//    public void registerWaitReceive(String id, CountDownLatch thread) {
//        waitReceiveThread.put(id, thread);
//    }
//
//    public static class ProjectEndpoint {
//        private Set<RequestMappingModel> controller = new HashSet<>();
//        private Set<SpringScheduledSpringInvokeEndpoint> scheduled = new HashSet<>();
//
//        public Set<RequestMappingModel> getController() {
//            return controller;
//        }
//
//        public void setController(Set<RequestMappingModel> controller) {
//            this.controller = controller;
//        }
//
//        public Set<SpringScheduledSpringInvokeEndpoint> getScheduled() {
//            return scheduled;
//        }
//
//        public void setScheduled(Set<SpringScheduledSpringInvokeEndpoint> scheduled) {
//            this.scheduled = scheduled;
//        }
//    }
}
