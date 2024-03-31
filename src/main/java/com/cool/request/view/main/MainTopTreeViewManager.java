/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * MainTopTreeViewManager.java is part of Cool Request
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

import com.cool.request.common.bean.components.BasicComponent;
import com.cool.request.common.bean.components.Component;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.state.CustomControllerFolderPersistent;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.components.ComponentType;
import com.cool.request.components.JavaClassComponent;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.CustomController;
import com.cool.request.components.scheduled.BasicScheduled;
import com.cool.request.components.scheduled.SpringScheduled;
import com.cool.request.components.scheduled.XxlJobScheduled;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.tool.Provider;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.*;

public class MainTopTreeViewManager implements Provider, CoolRequestIdeaTopic.ComponentAddEvent {
    private final MainTopTreeView mainTopTreeView;
    private final Project project;
    private JTreeAppearance jTreeAppearance;
    private final Map<MainTopTreeView.TreeNode<?>, List<MainTopTreeView.RequestMappingNode>> requestMappingNodeMap = new HashMap<>();//类名节点->所有实例节点
    private final Map<MainTopTreeView.TreeNode<?>, List<MainTopTreeView.BasicScheduledMethodNode<?>>> scheduleMapNodeMap = new HashMap<>();//类名节点->所有实例节点
    private final NodeFactory defaultNodeFactory = new DefaultNodeFactory();
    private int currentJTreeMode;
    private final Map<String, MainTopTreeView.TreeNode<?>> controllerIdMap = new HashMap<>();

    private MainTopTreeView.FeaturesModuleNode getFeaturesModuleNode(Component component) {
        if (component instanceof BasicScheduled) {
            return mainTopTreeView.getScheduledFeaturesModuleNode();
        }
        if (component instanceof Controller) {
            return mainTopTreeView.getControllerFeaturesModuleNode();
        }
        return null;
    }

    public Map<String, MainTopTreeView.TreeNode<?>> getControllerIdMap() {
        return controllerIdMap;
    }

    /**
     * 监听组件被添加
     *
     * @param components
     * @param componentType
     */
    @Override
    public void addComponent(List<? extends Component> components, ComponentType componentType) {
        if (components == null || components.isEmpty()) {
            return;
        }
        for (Component component : components) {
            if (component instanceof JavaClassComponent) {
                JavaClassComponent javaClassComponent = (JavaClassComponent) component;
                MainTopTreeView.FeaturesModuleNode featuresModuleNode = getFeaturesModuleNode(component);
                MainTopTreeView.ProjectModuleNode projectModuleNode = findExistingProjectModuleNodes(javaClassComponent
                        .getUserProjectModuleName(), featuresModuleNode);

                MainTopTreeView.TreeNode<?> classNameNode = null;
                if (component instanceof Controller) {
                    classNameNode = this.jTreeAppearance.getClassNameNode(projectModuleNode, javaClassComponent.getJavaClassName(), requestMappingNodeMap);
                } else if (component instanceof BasicScheduled) {
                    classNameNode = this.jTreeAppearance.getClassNameNode(projectModuleNode, javaClassComponent.getJavaClassName(), scheduleMapNodeMap);
                } else {
                    classNameNode = this.jTreeAppearance.getClassNameNode(projectModuleNode, javaClassComponent.getJavaClassName(), new HashMap<>());
                }
                MainTopTreeView.TreeNode<?> finalClassNameNode = classNameNode;
                MainTopTreeView.TreeNode<?> treeNode = defaultNodeFactory.factoryTreeNode(component);
                if (treeNode != null) {
                    MainTopTreeView.TreeNode<?> requestMappingNode = getRequestMappingNodeFromParentNode(classNameNode, component);
                    controllerIdMap.put(component.getId(), requestMappingNode);
                    if (requestMappingNode == null) {
                        classNameNode.add(treeNode); //添加节点
                        SwingUtilities.invokeLater(() -> ((DefaultTreeModel) mainTopTreeView.getTree().getModel()).reload(finalClassNameNode));

                        if (treeNode instanceof MainTopTreeView.RequestMappingNode) {
                            requestMappingNodeMap.get(classNameNode).add(((MainTopTreeView.RequestMappingNode) treeNode));
                        }
                        if (treeNode instanceof MainTopTreeView.BasicScheduledMethodNode) {
                            scheduleMapNodeMap.get(classNameNode).add(((MainTopTreeView.BasicScheduledMethodNode<?>) treeNode));
                        }
                    } else {
                        requestMappingNode.setUserObject(component);

                        SwingUtilities.invokeLater(() -> ((DefaultTreeModel) mainTopTreeView.getTree().getModel()).nodeChanged(finalClassNameNode));
                    }

                }
            }
        }
        mainTopTreeView.getRoot()
                .setUserObject(getControllerCount() + " api");
    }

    private int getControllerCount() {
        int result = 0;
        for (List<MainTopTreeView.RequestMappingNode> value : requestMappingNodeMap.values()) {
            result += value.size();
        }
        return result;
    }

    //    private
    private MainTopTreeView.TreeNode<?> getRequestMappingNodeFromParentNode(MainTopTreeView.TreeNode<?> classNameNode, Component component) {
        for (int i = 0; i < classNameNode.getChildCount(); i++) {
            TreeNode treeNode = classNameNode.getChildAt(i);
            if (treeNode instanceof MainTopTreeView.TreeNode) {
                Object data = ((MainTopTreeView.TreeNode<?>) treeNode).getData();
                if (data instanceof BasicComponent) {
                    if (StringUtils.isEqualsIgnoreCase(((BasicComponent) data).getId(), component.getId())) {
                        return ((MainTopTreeView.TreeNode<?>) treeNode);
                    }
                }

            }

        }
        return null;
    }

    private MainTopTreeView.ProjectModuleNode findExistingProjectModuleNodes(String moduleName, MainTopTreeView.FeaturesModuleNode targetFeaturesModuleNode) {
        for (int i = 0; i < targetFeaturesModuleNode.getChildCount(); i++) {
            javax.swing.tree.TreeNode node = targetFeaturesModuleNode.getChildAt(i);
            if (node instanceof MainTopTreeView.ProjectModuleNode) {
                boolean equals = ((MainTopTreeView.ProjectModuleNode) node).getData().equals(moduleName);
                if (equals) return ((MainTopTreeView.ProjectModuleNode) node);
            }
        }
        MainTopTreeView.ProjectModuleNode projectModuleNode = new MainTopTreeView.ProjectModuleNode(moduleName);
        targetFeaturesModuleNode.add(projectModuleNode);
        SwingUtilities.invokeLater(() -> {
            List<TreePath> treePaths = TreeUtil.collectExpandedPaths(mainTopTreeView.getTree());
            ((DefaultTreeModel) mainTopTreeView.getTree().getModel()).reload(targetFeaturesModuleNode);
            for (TreePath treePath : treePaths) {
                mainTopTreeView.getTree().expandPath(treePath);
            }
        });
        return projectModuleNode;
    }

    public MainTopTreeViewManager(MainTopTreeView mainTopTreeView, Project project) {
        this.mainTopTreeView = mainTopTreeView;
        this.project = project;
        MessageBusConnection messageBusConnection = project.getMessageBus().connect();

        messageBusConnection.subscribe(CoolRequestIdeaTopic.COMPONENT_ADD, this);

        initTreeAppearanceMode();

        messageBusConnection.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE, () -> {
            if (SettingPersistentState.getInstance().getState().treeAppearanceMode != currentJTreeMode) {
                initTreeAppearanceMode();
                changeTreeAppearance();
            }

        });
        messageBusConnection.subscribe(CoolRequestIdeaTopic.REFRESH_CUSTOM_FOLDER, this::addCustomController);

        messageBusConnection.subscribe(CoolRequestIdeaTopic.DELETE_ALL_DATA, this::clearData);
        addCustomController();
    }

    public void clearData() {
        mainTopTreeView.getControllerFeaturesModuleNode().removeAllChildren();
        mainTopTreeView.getScheduledFeaturesModuleNode().removeAllChildren();
        requestMappingNodeMap.clear();
        scheduleMapNodeMap.clear();
        mainTopTreeView.getRoot().setUserObject("0 mapper");
        SwingUtilities.invokeLater(() -> mainTopTreeView.getTree().updateUI());
    }

    private void changeTreeAppearance() {
        clearData();
        addCustomController();
        UserProjectManager.getInstance(project)
                .getProjectComponents()
                .forEach((componentType, components) -> addComponent(components, componentType));
    }

    public void addCustomController() {
        CustomControllerFolderPersistent.Folder folder = CustomControllerFolderPersistent.getInstance().getFolder();

        buildCustomController(this.mainTopTreeView.getControllerFeaturesModuleNode(), folder);
        this.mainTopTreeView.getTree().updateUI();
    }

    /**
     * 构建自定义目录，保持自定义目录一直在顶部
     */
    private void buildCustomController(MainTopTreeView.TreeNode<?> treeNode, CustomControllerFolderPersistent.Folder folder) {
        List<CustomController> customControllers = Optional.ofNullable(folder.getControllers()).orElse(new ArrayList<>());
        List<MainTopTreeView.CustomMappingNode> currentNodeCustomMappingNodes = listNodesFromTreeNode(treeNode, MainTopTreeView.CustomMappingNode.class);
        currentNodeCustomMappingNodes.stream()
                .filter(customMappingNode -> canRemoveController(customControllers, customMappingNode))
                .forEach(treeNode::remove);

        customControllers.stream()
                .filter(controller -> !containsCustomController(treeNode, controller))
                .forEach(controller -> {
                    int insertIndex = treeNode == mainTopTreeView.getControllerFeaturesModuleNode() ? 0 : treeNode.getChildCount();
                    treeNode.insert(new MainTopTreeView.CustomMappingNode(controller), insertIndex);
                });

        List<CustomControllerFolderPersistent.Folder> folderItems = folder.getItems();
        List<MainTopTreeView.CustomControllerFolderNode> customControllerFolderNodes = listNodesFromTreeNode(treeNode, MainTopTreeView.CustomControllerFolderNode.class);
        customControllerFolderNodes.stream()
                .filter(customControllerFolderNode -> canRemoveFolder(folderItems, customControllerFolderNode))
                .forEach(treeNode::remove);

        for (CustomControllerFolderPersistent.Folder item : folderItems) {
            MainTopTreeView.CustomControllerFolderNode customControllerFolderNode = containsFolder(treeNode, item)
                    ? getFolderNode(treeNode, item)
                    : new MainTopTreeView.CustomControllerFolderNode(item);

            if (!containsFolder(treeNode, item)) {
                int insertIndex = treeNode == mainTopTreeView.getControllerFeaturesModuleNode() ? 0 : treeNode.getChildCount();
                treeNode.insert(customControllerFolderNode, insertIndex);
            }
            buildCustomController(customControllerFolderNode, item);
        }
    }

    private MainTopTreeView.CustomControllerFolderNode getFolderNode(MainTopTreeView.TreeNode<?> treeNode, CustomControllerFolderPersistent.Folder folder) {
        for (int i = 0; i < treeNode.getChildCount(); i++) {
            javax.swing.tree.TreeNode child = treeNode.getChildAt(i);
            if (child instanceof MainTopTreeView.CustomControllerFolderNode) {
                if (Objects.equals(((MainTopTreeView.CustomControllerFolderNode) child).getData().getName(), folder.getName())) {
                    return ((MainTopTreeView.CustomControllerFolderNode) child);
                }
            }
        }
        return null;
    }

    private boolean containsCustomController(MainTopTreeView.TreeNode<?> treeNode, CustomController controller) {
        for (int i = 0; i < treeNode.getChildCount(); i++) {
            javax.swing.tree.TreeNode child = treeNode.getChildAt(i);
            if (child instanceof MainTopTreeView.CustomMappingNode) {
                if (Objects.equals(((MainTopTreeView.CustomMappingNode) child).getData().getId(), controller.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsFolder(MainTopTreeView.TreeNode<?> treeNode, CustomControllerFolderPersistent.Folder folder) {
        for (int i = 0; i < treeNode.getChildCount(); i++) {
            javax.swing.tree.TreeNode child = treeNode.getChildAt(i);
            if (child instanceof MainTopTreeView.CustomControllerFolderNode) {
                if (Objects.equals(((MainTopTreeView.CustomControllerFolderNode) child).getData().getName(), folder.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canRemoveController(List<CustomController> customControllers, MainTopTreeView.CustomMappingNode customMappingNode) {
        return customControllers.stream()
                .noneMatch(controller -> Objects.equals(controller.getId(), customMappingNode.getData().getId()));
    }

    public <T extends javax.swing.tree.TreeNode> List<T> listNodesFromTreeNode(MainTopTreeView.TreeNode<?> treeNode, Class<T> nodeType) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < treeNode.getChildCount(); i++) {
            javax.swing.tree.TreeNode child = treeNode.getChildAt(i);
            if (nodeType.isInstance(child)) {
                result.add(nodeType.cast(child));
            }
        }
        return result;
    }

    private boolean canRemoveFolder(List<CustomControllerFolderPersistent.Folder> customControllers,
                                    MainTopTreeView.CustomControllerFolderNode customControllerFolderNode) {
        return customControllers.stream()
                .noneMatch(folder -> Objects.equals(folder.getName(), customControllerFolderNode.getData().getName()));
    }

    private void initTreeAppearanceMode() {
        currentJTreeMode = SettingPersistentState.getInstance().getState().treeAppearanceMode;
        if (currentJTreeMode == 0) jTreeAppearance = new DefaultJTreeAppearance();
        if (currentJTreeMode == 1) jTreeAppearance = new FlattenAppearance();
        if (currentJTreeMode == 2) jTreeAppearance = new NoAppearance();
    }

    private interface NodeFactory {
        public MainTopTreeView.TreeNode<?> factoryTreeNode(Component component);
    }

    public static class DefaultNodeFactory implements NodeFactory {
        @Override
        public MainTopTreeView.TreeNode<?> factoryTreeNode(Component component) {
            if (component instanceof Controller)
                return new MainTopTreeView.RequestMappingNode(((Controller) component));

            if (component instanceof SpringScheduled)
                return new MainTopTreeView.SpringScheduledMethodNode(((SpringScheduled) component));

            if (component instanceof XxlJobScheduled)
                return new MainTopTreeView.XxlJobMethodNode(((XxlJobScheduled) component));
            return null;
        }
    }

    private interface JTreeAppearance {
        <T> MainTopTreeView.TreeNode<?> getClassNameNode(MainTopTreeView.ProjectModuleNode projectModuleNode, String className,
                                                         Map<MainTopTreeView.TreeNode<?>, List<T>> targetNodeMap);
    }

    private class DefaultJTreeAppearance implements JTreeAppearance {

        @Override
        public <T> MainTopTreeView.ClassNameNode getClassNameNode(MainTopTreeView.ProjectModuleNode projectModuleNode, String className,
                                                                  Map<MainTopTreeView.TreeNode<?>, List<T>> targetNodeMap) {
            for (int i = 0; i < projectModuleNode.getChildCount(); i++) {
                javax.swing.tree.TreeNode node = projectModuleNode.getChildAt(i);
                if (node instanceof MainTopTreeView.ClassNameNode) {
                    boolean equals = ((MainTopTreeView.ClassNameNode) node).getData().equals(className);
                    if (equals) return ((MainTopTreeView.ClassNameNode) node);
                }
            }

            MainTopTreeView.ClassNameNode classNameNode = new MainTopTreeView.ClassNameNode(className);
            projectModuleNode.add(classNameNode);
            targetNodeMap.put(classNameNode, new ArrayList<>());
            SwingUtilities.invokeLater(() -> ((DefaultTreeModel) mainTopTreeView.getTree().getModel()).reload(projectModuleNode));
            return classNameNode;
        }
    }

    private static class NoAppearance implements JTreeAppearance {
        @Override
        public <T> MainTopTreeView.TreeNode<?> getClassNameNode(MainTopTreeView.ProjectModuleNode projectModuleNode, String className,
                                                                Map<MainTopTreeView.TreeNode<?>, List<T>> targetNodeMap) {
            targetNodeMap.computeIfAbsent(projectModuleNode, classNameNode1 -> new ArrayList<>());
            return projectModuleNode;
        }
    }

    private class FlattenAppearance implements JTreeAppearance {
        @Override
        public <T> MainTopTreeView.ClassNameNode getClassNameNode(MainTopTreeView.ProjectModuleNode projectModuleNode, String className,
                                                                  Map<MainTopTreeView.TreeNode<?>, List<T>> targetNodeMap) {
            String[] parts = className.split("\\.");
            MainTopTreeView.ClassNameNode classNameNode = buildClassNameLevelNode(projectModuleNode, parts, 0);
            targetNodeMap.computeIfAbsent(classNameNode, classNameNode1 -> new ArrayList<>());
            return classNameNode;
        }

        private MainTopTreeView.ClassNameNode buildClassNameLevelNode(DefaultMutableTreeNode parent, String[] parts, int index) {
            if (index >= parts.length) {
                return (MainTopTreeView.ClassNameNode) parent;
            }
            String part = parts[index];
            int childCount = parent.getChildCount();
            MainTopTreeView.ClassNameNode node = null;
            for (int i = 0; i < childCount; i++) {
                MainTopTreeView.ClassNameNode childNode = (MainTopTreeView.ClassNameNode) parent.getChildAt(i);
                if (StringUtils.isEqualsIgnoreCase(childNode.getData(), part)) {
                    node = childNode;
                    break;
                }
            }
            if (node == null) {
                node = (index == parts.length - 1) ? new MainTopTreeView.ClassNameNode(part) : new MainTopTreeView.PackageNameNode(part);
                parent.add(node);
                SwingUtilities.invokeLater(() -> ((DefaultTreeModel) mainTopTreeView.getTree().getModel()).reload(parent));
            }
            return buildClassNameLevelNode(node, parts, index + 1);
        }
    }

    public @NotNull Map<MainTopTreeView.TreeNode<?>, List<MainTopTreeView.RequestMappingNode>> getRequestMappingNodeMap() {
        return requestMappingNodeMap;
    }

    public @NotNull Map<MainTopTreeView.TreeNode<?>, List<MainTopTreeView.BasicScheduledMethodNode<?>>> getScheduleMapNodeMap() {
        return scheduleMapNodeMap;
    }
}
