package com.cool.request.view.tool;

import com.cool.request.common.bean.RefreshInvokeRequestBody;
import com.cool.request.common.bean.components.BasicComponent;
import com.cool.request.common.bean.components.Component;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.DynamicController;
import com.cool.request.common.bean.components.controller.StaticController;
import com.cool.request.common.bean.components.scheduled.BasicScheduled;
import com.cool.request.common.bean.components.scheduled.DynamicSpringScheduled;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.model.InvokeReceiveModel;
import com.cool.request.common.model.ProjectStartupModel;
import com.cool.request.component.ComponentType;
import com.cool.request.component.JavaClassComponent;
import com.cool.request.component.http.DynamicDataManager;
import com.cool.request.component.http.invoke.InvokeResult;
import com.cool.request.component.http.invoke.RefreshComponentRequest;
import com.cool.request.utils.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class UserProjectManager implements Provider {
    /**
     * 每个项目可以启动N个SpringBoot实例，但是端口会不一样
     */
    private final List<ProjectStartupModel> springBootApplicationStartupModel = new ArrayList<>();
    private final Map<String, CountDownLatch> waitReceiveThread = new HashMap<>();
    private final Project project;
    private final Map<String, String> dynamicControllerIdMap = new HashMap<>();
    private final Map<String, String> dynamicScheduleIdMap = new HashMap<>();
    private final CoolRequest coolRequest;
    //项目所有的组件数据
    private final Map<ComponentType, List<Component>> projectComponents = new HashMap<>();
    private final Map<ComponentType, ComponentRegisterAction> componentTypeComponentRegisterActionMap = new HashMap<>();

    public UserProjectManager(Project project, CoolRequest coolRequest) {
        this.project = project;
        this.coolRequest = coolRequest;
        this.project.getMessageBus().connect().subscribe(CoolRequestIdeaTopic.DELETE_ALL_DATA,
                (CoolRequestIdeaTopic.DeleteAllDataEventListener) this::clear);

        registerComponentRegisterAction(ComponentType.CONTROLLER, this::controllerAddEvent);
        registerComponentRegisterAction(ComponentType.SCHEDULE, this::scheduledAddEvent);

    }

    public <T extends Component> List<T> getComponentByType(Class<T> typeClass) {
        List<T> result = new ArrayList<>();
        for (List<Component> value : projectComponents.values()) {
            for (Component component : value) {
                if (typeClass.isAssignableFrom(component.getClass())) {
                    result.add((T) component);
                }
            }
        }
        return result;
    }

    public void refreshComponents() {
        project.putUserData(CoolRequestConfigConstant.ServerMessageRefreshModelSupplierKey, () -> Boolean.TRUE);
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Refresh") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Set<Integer> failPort = new HashSet<>();
                if (springBootApplicationStartupModel.isEmpty()) {
                    NotifyUtils.notification(project, ResourceBundleUtils.getString("dynamic.refresh.fail.no.port"));
                    return;
                }
                for (ProjectStartupModel projectStartupModel : springBootApplicationStartupModel) {
                    InvokeResult invokeResult = new RefreshComponentRequest(projectStartupModel.getPort()).requestSync(new RefreshInvokeRequestBody());
                    if (invokeResult == InvokeResult.FAIL) failPort.add(projectStartupModel.getProjectPort());
                }
                if (!failPort.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        String ports = failPort.stream().map(String::valueOf)
                                .collect(Collectors.joining("、"));
                        Messages.showErrorDialog(ResourceBundleUtils.getString("unable.refresh") + " " + ports, ResourceBundleUtils.getString("tip"));
                    });
                }
            }
        });
    }

    private int findById(Component target, List<Component> components) {
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            if (StringUtils.isEqualsIgnoreCase(component.getId(), target.getId())) {
                return i;
            }

        }
        return -1;
    }

    private void componentCopy(Component oldComponent, Component newComponent) {
        if (oldComponent instanceof StaticController && newComponent instanceof DynamicController) {
            ((DynamicController) newComponent).setOwnerPsiMethod(((StaticController) oldComponent).getOwnerPsiMethod());
        }
        //设置默认的OwnerPsiMethod
        if (newComponent instanceof DynamicController) {
            if (((DynamicController) newComponent).getOwnerPsiMethod() == null || ((DynamicController) newComponent).getOwnerPsiMethod().isEmpty()) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    Module classNameModule = PsiUtils.findClassNameModule(project, ((DynamicController) newComponent).getJavaClassName());
                    if (classNameModule != null) {
                        PsiClass psiClass = PsiUtils.findClassByName(classNameModule.getProject(), classNameModule, ((DynamicController) newComponent).getJavaClassName());
                        if (psiClass != null) {
                            PsiMethod httpMethodInClass = PsiUtils.findHttpMethodInClass(psiClass, ((DynamicController) newComponent));
                            if (httpMethodInClass != null) {
                                ((DynamicController) newComponent).setOwnerPsiMethod(List.of(httpMethodInClass));

                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 所有组件数据统一走这里添加
     */
    public void addComponent(ComponentType componentType, List<? extends Component> data) {
        if (data == null || data.isEmpty()) return;
        //添加挤压数据中，主窗口打开后推送
        if (!coolRequest.canAddComponentToView()) {
            coolRequest.addBacklogData(componentType, data);
            return;
        }

        for (Component component : data) {
            //java组件数据初始化
            if (component instanceof JavaClassComponent) {
                ComponentUtils.init(project, ((JavaClassComponent) component));
            }
            //id初始化
            if (component instanceof BasicComponent) {
                if (StringUtils.isEmpty(component.getId())) ((BasicComponent) component).calcId(project);
            }

            List<Component> components = projectComponents.computeIfAbsent(componentType, (v) -> new ArrayList<>());
            int i = findById(component, components);
            if (i >= 0) {
                componentCopy(components.get(i), component);
                components.set(i, component);
            } else {
                components.add(component);
            }

            //可能处于拉取状态
            if (component instanceof DynamicController) {
                DynamicDataManager.getInstance(project).dataNotify(((DynamicController) component));
            }
        }
        //广播组件被添加
        this.project.getMessageBus()
                .syncPublisher(CoolRequestIdeaTopic.COMPONENT_ADD)
                .addComponent(data, componentType);

        //每种类型被添加前执行的操作
        componentTypeComponentRegisterActionMap.getOrDefault(componentType, components -> {
        }).invoke(data);

    }

    private void registerComponentRegisterAction(ComponentType componentType, ComponentRegisterAction componentRegisterAction) {
        componentTypeComponentRegisterActionMap.put(componentType, componentRegisterAction);
    }

    public Map<ComponentType, List<Component>> getProjectComponents() {
        return projectComponents;
    }

    public void addSpringBootApplicationInstance(int projectPort, int startPort) {
        springBootApplicationStartupModel.add(new ProjectStartupModel(projectPort, startPort));
    }

    private void controllerAddEvent(List<? extends Component> controllers) {
        for (Component controller : controllers) {
            if (controller instanceof DynamicController) {
                dynamicControllerIdMap.put(((DynamicController) controller).getSpringInnerId(), controller.getId());
            }
        }


    }

    private void scheduledAddEvent(List<? extends Component> scheduleds) {
        for (Component controller : scheduleds) {
            if (controller instanceof DynamicSpringScheduled) {
                dynamicScheduleIdMap.put(((DynamicSpringScheduled) controller).getSpringInnerId(), controller.getId());
            }
        }
//
//        this.project.getMessageBus()
//                .syncPublisher(CoolRequestIdeaTopic.ADD_SPRING_SCHEDULED_MODEL)
//                .addSpringScheduledModel(scheduleds);
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

    public List<ProjectStartupModel> getSpringBootApplicationStartupModel() {
        return springBootApplicationStartupModel;
    }

    public void clear() {
        projectComponents.clear();
    }

    public Project getProject() {
        return project;
    }

    public List<Controller> getController() {
        return getComponentByType(Controller.class);
    }

    public List<BasicScheduled> getScheduled() {
        return getComponentByType(BasicScheduled.class);
    }

    public interface ComponentRegisterAction {
        public void invoke(List<? extends Component> components);
    }
}
