package com.cool.request.view.tool;

import com.cool.request.common.bean.components.BasicComponent;
import com.cool.request.common.bean.components.Component;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.model.ProjectStartupModel;
import com.cool.request.components.ComponentConverter;
import com.cool.request.components.ComponentType;
import com.cool.request.components.JavaClassComponent;
import com.cool.request.components.convert.DynamicControllerComponentConverter;
import com.cool.request.components.http.Controller;
import com.cool.request.components.scheduled.BasicScheduled;
import com.cool.request.utils.ComponentUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.UrlUtils;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProjectManager implements Provider {
    /**
     * 每个项目可以启动N个SpringBoot实例，但是端口会不一样
     */
    private final List<ProjectStartupModel> springBootApplicationStartupModel = new ArrayList<>();
    private final List<ComponentConverter<? extends Component, ? extends Component>> componentConverters = new ArrayList<>();
    private final Project project;
    private final CoolRequest coolRequest;
    //项目所有的组件数据
    private final Map<ComponentType, List<Component>> projectComponents = new HashMap<>();
    private final Map<ComponentType, ComponentRegisterAction> componentTypeComponentRegisterActionMap = new HashMap<>();

    public UserProjectManager(Project project, CoolRequest coolRequest) {
        this.project = project;
        this.coolRequest = coolRequest;
        this.project.getMessageBus().connect().subscribe(CoolRequestIdeaTopic.DELETE_ALL_DATA, this::clear);

        componentConverters.add(new DynamicControllerComponentConverter());
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
//        project.putUserData(CoolRequestConfigConstant.ServerMessageRefreshModelSupplierKey, () -> Boolean.TRUE);
//        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Refresh") {
//            @Override
//            public void run(@NotNull ProgressIndicator indicator) {
//                Set<Integer> failPort = new HashSet<>();
//                if (springBootApplicationStartupModel.isEmpty()) {
//                    NotifyUtils.notification(project, ResourceBundleUtils.getString("dynamic.refresh.fail.no.port"));
//                    return;
//                }
//                for (ProjectStartupModel projectStartupModel : springBootApplicationStartupModel) {
//                    InvokeResult invokeResult = new RefreshComponentRequest(projectStartupModel.getPort()).requestSync(new RefreshInvokeRequestBody());
//                    if (invokeResult == InvokeResult.FAIL) failPort.add(projectStartupModel.getProjectPort());
//                }
//                if (!failPort.isEmpty()) {
//                    SwingUtilities.invokeLater(() -> {
//                        String ports = failPort.stream().map(String::valueOf)
//                                .collect(Collectors.joining("、"));
//                        Messages.showErrorDialog(ResourceBundleUtils.getString("unable.refresh") + " " + ports, ResourceBundleUtils.getString("tip"));
//                    });
//                }
//            }
//        });
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

    private Component convertComponent(Component oldComponent, Component newComponent) {
        for (ComponentConverter<? extends Component, ? extends Component> componentConverter : componentConverters) {
            if (componentConverter.canSupport(oldComponent, newComponent)) {
                newComponent = componentConverter.converter(project, oldComponent, newComponent);
            }
        }
        return newComponent;
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
                components.set(i, convertComponent(components.get(i), component));
            } else {
                components.add(convertComponent(null, component));
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

    public List<ProjectStartupModel> getSpringBootApplicationStartupModel() {
        return springBootApplicationStartupModel;
    }

    public int getRMIPortByProjectPort(int port) {
        for (ProjectStartupModel projectStartupModel : springBootApplicationStartupModel) {
            if (projectStartupModel.getProjectPort() == port) return projectStartupModel.getPort();
        }
        return -1;
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
