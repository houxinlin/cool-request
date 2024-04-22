/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * UserProjectManager.java is part of Cool Request
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

package com.cool.request.view.tool;

import com.cool.request.common.bean.components.BasicComponent;
import com.cool.request.common.bean.components.Component;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.model.ProjectStartupModel;
import com.cool.request.components.ComponentType;
import com.cool.request.components.JavaClassComponent;
import com.cool.request.components.http.Controller;
import com.cool.request.components.scheduled.BasicScheduled;
import com.cool.request.utils.ComponentUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.*;

@Service
public final class UserProjectManager {
    /**
     * 每个项目可以启动N个SpringBoot实例，但是端口会不一样
     */
    private final List<ProjectStartupModel> springBootApplicationStartupModel = new ArrayList<>();
    private final Project project;
    //项目所有的组件数据
    private final Map<ComponentType, List<Component>> projectComponents = new HashMap<>();

    public UserProjectManager(Project project) {
        this.project = project;
    }

    public static UserProjectManager getInstance(Project project) {
        return project.getService(UserProjectManager.class);
    }

    public UserProjectManager init() {
        return this;
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

    private int findById(Component target, List<Component> components) {
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            if (StringUtils.isEqualsIgnoreCase(component.getId(), target.getId())) {
                return i;
            }
        }
        return -1;
    }

    public void markComponentState(Class<?> componentTypeClass, boolean state) {
        projectComponents.forEach((componentType, components) -> {
            for (Component component : components) {
                if (componentTypeClass.isAssignableFrom(component.getClass())) {
                    component.setAvailable(state);
                }
            }
        });
    }

    public void deleteNotAvailableComponent(Class<?> componentTypeClass) {
        projectComponents.forEach((componentType, components) -> {
            Iterator<Component> iterator = components.iterator();
            while (iterator.hasNext()) {
                Component component = iterator.next();
                if (componentTypeClass.isAssignableFrom(component.getClass())) {
                    if (component.isAvailable()) continue;
                    iterator.remove();
                    project.getMessageBus()
                            .syncPublisher(CoolRequestIdeaTopic.COMPONENT_DELETE_EVENT)
                            .deleteComponent(component);
                }
            }
        });
    }

    /**
     * 所有组件数据统一走这里添加
     */
    public void addComponent(ComponentType componentType, List<? extends Component> data) {
        if (data == null || data.isEmpty()) return;
        for (Component newComponent : data) {
            initComponent(newComponent);
            List<Component> components = projectComponents.computeIfAbsent(componentType, (v) -> new ArrayList<>());
            int index = findById(newComponent, components);
            if (index < 0) {
                components.add(newComponent);
            } else {
                components.get(index).setAvailable(Boolean.TRUE);
            }
        }
        project.getMessageBus()
                .syncPublisher(CoolRequestIdeaTopic.COMPONENT_ADD)
                .addComponent(data);
    }

    public Map<ComponentType, List<Component>> getProjectComponents() {
        return projectComponents;
    }

    public void addSpringBootApplicationInstance(int projectPort, int startPort) {
        springBootApplicationStartupModel.removeIf(projectStartupModel -> projectStartupModel.getProjectPort() == projectPort);
        springBootApplicationStartupModel.add(new ProjectStartupModel(projectPort, startPort));
    }

    private void initComponent(Component newComponent) {
        //java组件数据初始化
        newComponent.setAvailable(Boolean.TRUE);
        if (newComponent instanceof JavaClassComponent) {
            ComponentUtils.init(project, ((JavaClassComponent) newComponent));
        }
        //id初始化
        if (newComponent instanceof BasicComponent) {
            if (StringUtils.isEmpty(newComponent.getId())) ((BasicComponent) newComponent).calcId(project);
        }
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
}
