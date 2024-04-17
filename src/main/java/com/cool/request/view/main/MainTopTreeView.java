/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * MainTopTreeView.java is part of Cool Request
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

import com.cool.request.action.CleanCacheAnAction;
import com.cool.request.action.actions.*;
import com.cool.request.action.controller.CollapseSelectedAction;
import com.cool.request.action.controller.ExpandSelectedAction;
import com.cool.request.action.copy.*;
import com.cool.request.action.export.ApifoxExportAnAction;
import com.cool.request.action.export.ApipostExportAnAction;
import com.cool.request.action.export.OpenApiExportAnAction;
import com.cool.request.common.bean.components.Component;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.common.state.CustomControllerFolderPersistent;
import com.cool.request.common.state.MarkPersistent;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.components.CanDelete;
import com.cool.request.components.CanMark;
import com.cool.request.components.CodeNavigation;
import com.cool.request.components.CoolRequestPluginDisposable;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.CustomController;
import com.cool.request.components.scheduled.SpringScheduled;
import com.cool.request.components.scheduled.XxlJobScheduled;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.RestfulTreeCellRenderer;
import com.cool.request.view.component.CoolRequestView;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.tool.Provider;
import com.cool.request.view.tool.ProviderManager;
import com.cool.request.view.tool.ToolActionPageSwitcher;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ArrayUtil;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainTopTreeView extends JPanel implements Provider {
    private final Tree tree = new SimpleTree();
    private final Project project;
    private final RootNode root = new RootNode(("0 mapper"));
    private final FeaturesModuleNode controllerFeaturesModuleNode = new FeaturesModuleNode("Controller");
    private final FeaturesModuleNode scheduledFeaturesModuleNode = new FeaturesModuleNode("Scheduled");
    private final DefaultActionGroup exportActionGroup = new DefaultActionGroup("Export", true);
    private final DefaultActionGroup copyActionGroup = new DefaultActionGroup("Copy", true);
    private TreeNode<?> currentTreeNode;
    private final CoolRequestView coolRequestView;
    private final CleanCacheAnAction cleanCacheAnAction;

    private boolean isSelected(TreePath path) {
        TreePath[] selectionPaths = tree.getSelectionPaths();
        return selectionPaths != null && ArrayUtil.contains(path, selectionPaths);
    }

    public TreeNode<?> getCurrentTreeNode() {
        return currentTreeNode;
    }

    protected void invokeContextMenu(@NotNull final MouseEvent e, @NotNull ActionGroup actionGroup) {
        JPopupMenu component = ActionManager.getInstance().createActionPopupMenu("right", actionGroup).getComponent();
        component.show(e.getComponent(), e.getX(), e.getY());
    }

    public CoolRequestView getApiToolPage() {
        return coolRequestView;
    }

    public MainTopTreeView(Project project, CoolRequestView coolRequestView) {
        this.project = project;
        this.coolRequestView = coolRequestView;
        this.cleanCacheAnAction = new CleanCacheAnAction(this);
        ProviderManager.registerProvider(MainTopTreeView.class, CoolRequestConfigConstant.MainTopTreeViewKey, this, project);
        this.setLayout(new BorderLayout());

        JPanel progressJpanel = new JPanel(new BorderLayout());
        TreeUtil.installActions(tree);

        tree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    triggerNodeChooseEvent(false, false);
                }
            }
        });
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                invokePopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                invokePopup(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // 双击Api后跳转到请求界面
                    triggerNodeChooseEvent(true, true);
                    TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(tree);
                    if (selectedPathIfOne != null &&
                            (selectedPathIfOne.getLastPathComponent() instanceof RequestMappingNode ||
                                    selectedPathIfOne.getLastPathComponent() instanceof BasicScheduledMethodNode)) {
                        ProviderManager.findAndConsumerProvider(ToolActionPageSwitcher.class, project, toolActionPageSwitcher -> {
                            toolActionPageSwitcher.goToByName(MainBottomHTTPContainer.PAGE_NAME,
                                    selectedPathIfOne.getLastPathComponent());
                        });
                        return;
                    }
                }
                invokePopup(e);
            }

            private void invokePopup(final MouseEvent e) {
                exportActionGroup.getTemplatePresentation().setIcon(KotlinCoolRequestIcons.INSTANCE.getEXPORT().invoke());
                copyActionGroup.getTemplatePresentation().setIcon(KotlinCoolRequestIcons.INSTANCE.getCOPY().invoke());
                if (e.isPopupTrigger() && insideTreeItemsArea(e)) {
                    TreePath pathForLocation = selectPathUnderCursorIfNeeded(e);
                    if (pathForLocation != null && pathForLocation.getLastPathComponent() instanceof RequestMappingNode) {
                        invokeContextMenu(e, getPopupActions(pathForLocation.getLastPathComponent(), exportActionGroup, copyActionGroup));
                    } else {
                        if (pathForLocation != null) {
                            invokeContextMenu(e, getPopupActions(pathForLocation.getLastPathComponent(), exportActionGroup));
                        }
                    }
                }
            }

            private TreePath selectPathUnderCursorIfNeeded(final MouseEvent e) {
                TreePath pathForLocation = tree.getClosestPathForLocation(e.getX(), e.getY());
                if (!isSelected(pathForLocation)) {
                    tree.setSelectionPath(pathForLocation);
                }
                return pathForLocation;
            }

            private boolean insideTreeItemsArea(MouseEvent e) {
                Rectangle rowBounds = tree.getRowBounds(tree.getRowCount() - 1);
                if (rowBounds == null) {
                    return false;
                }
                double lastItemBottomLine = rowBounds.getMaxY();
                return e.getY() <= lastItemBottomLine;
            }
        });
        //设置点击事件
        tree.addTreeSelectionListener(e -> triggerNodeChooseEvent(SettingPersistentState.getInstance().getState().autoNavigation, false));
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(new DefaultMutableTreeNode());
        tree.setCellRenderer(new RestfulTreeCellRenderer());
        tree.setRootVisible(true);
        tree.setShowsRootHandles(false);
        JBScrollPane mainJBScrollPane = new JBScrollPane();
        mainJBScrollPane.setViewportView(tree);
        progressJpanel.setOpaque(false);
        JProgressBar jProgressBar = new JProgressBar();
        jProgressBar.setValue(0);
        this.add(mainJBScrollPane, BorderLayout.CENTER);
        this.add(progressJpanel, BorderLayout.NORTH);
        root.add(controllerFeaturesModuleNode);
        root.add(scheduledFeaturesModuleNode);

        exportActionGroup.getTemplatePresentation().setIcon(CoolRequestIcons.EXPORT);
        exportActionGroup.add(new ApifoxExportAnAction(this));
        exportActionGroup.add(new ApipostExportAnAction((this)));
        // TODO: 2023/9/23 目前找到不到接口
        exportActionGroup.add(new OpenApiExportAnAction((this)));

        copyActionGroup.add(new CopyClassNameAnAction(this));
        copyActionGroup.add(new CopyCurlAnAction(this));
        copyActionGroup.add(new CopyHttpUrlAnAction(this));
        copyActionGroup.add(new CopyMethodNameAnAction(this));
        copyActionGroup.add(new CopyOpenApiAction(this));
        copyActionGroup.getTemplatePresentation().setIcon(CoolRequestIcons.COPY);

        MessageBusConnection connect = project.getMessageBus().connect();
        connect.subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE,
                (CoolRequestIdeaTopic.BaseListener) this::loadText);
        MessageBusConnection messageBusConnection = ApplicationManager.getApplication().getMessageBus()
                .connect();
        Disposer.register(CoolRequestPluginDisposable.getInstance(project), messageBusConnection);
        ((DefaultTreeModel) tree.getModel()).setRoot(root);

        loadText();
    }


    private void loadText() {
        exportActionGroup.getTemplatePresentation().setText(ResourceBundleUtils.getString("export"));
        copyActionGroup.getTemplatePresentation().setText(ResourceBundleUtils.getString("copy"));
        cleanCacheAnAction.getTemplatePresentation().setText(ResourceBundleUtils.getString("clear.request.cache"));
    }

    /**
     * 触发节点选中事件
     */
    private void triggerNodeChooseEvent(boolean navigate, boolean selectData) {
        if (!navigate) return;
        DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (lastSelectedPathComponent == null) return;
        Object userObject = lastSelectedPathComponent.getUserObject();
        if (lastSelectedPathComponent instanceof TreeNode) {
            currentTreeNode = ((TreeNode<?>) lastSelectedPathComponent);
        }
        if (userObject instanceof CodeNavigation) {
            ((CodeNavigation) userObject).goToCode(project);
            if (selectData) {
                if (userObject instanceof Component) {
                    project.getMessageBus()
                            .syncPublisher(CoolRequestIdeaTopic.COMPONENT_CHOOSE_EVENT)
                            .onChooseEvent(((Component) userObject));
                }
            }
        }
    }

    public Tree getTree() {
        return tree;
    }

    protected ActionGroup getPopupActions(Object node, AnAction... actions) {
        DefaultActionGroup group = new DefaultActionGroup();
        for (AnAction action : actions) {
            group.add(action);
        }
        group.addSeparator();
        // 可以增加自定义目录
        if (node != null && (node == controllerFeaturesModuleNode || node instanceof CustomControllerFolderNode)) {
            group.add(new AddCustomFolderAnAction(project, this));
        }
        if (node instanceof RequestMappingNode) {
            group.add(new OpenHTTPRequestPageTab(project, this, KotlinCoolRequestIcons.INSTANCE.getOPEN_IN_NEW_TAB().invoke()));

            if (((RequestMappingNode) node).getData() instanceof CustomController) {
                group.add(new CustomSummaryAnAction(project, this,  KotlinCoolRequestIcons.INSTANCE.getREMAKE().invoke()));
            }
        }
        group.addSeparator();
        cleanCacheAnAction.getTemplatePresentation().setIcon(KotlinCoolRequestIcons.INSTANCE.getCLEAR().invoke());
        group.add(cleanCacheAnAction);
        group.addSeparator();
        //节点数据是否可收藏
        if (node instanceof TreeNode && (((TreeNode<?>) node).getData() instanceof CanMark)) {
            Object data = ((TreeNode<?>) node).getData();
            if (data instanceof com.cool.request.common.bean.components.Component) {
                if (MarkPersistent.getInstance(project).in(((com.cool.request.common.bean.components.Component) data))) {
                    group.add(new UnMarkAnAction(project, this));
                } else {
                    group.add(new MarkNodeAnAction(project, this.tree));
                }
            }
        }
        //节点是否可删除
        if (node instanceof TreeNode && (node instanceof CanDelete)) {
            group.add(new DeleteCustomControllerAnAction(project, this));
        }

        group.add(new ExpandSelectedAction(tree, project));
        group.add(new CollapseSelectedAction(tree, project));
        return group;
    }

    public void selectNode(TreeNode<?> node) {
        if (node == null) return;
        this.currentTreeNode = node;

        SwingUtilities.invokeLater(() -> {
            TreePath treePath = new TreePath(node.getPath());
            TreeSelectionModel selectionModel = tree.getSelectionModel();
            if (selectionModel != null) {
                selectionModel.setSelectionPath(treePath);
            }
            tree.scrollPathToVisible(treePath);
            tree.updateUI();
        });
    }

    public List<Controller> getSelectController() {
        List<Controller> result = new ArrayList<>();
        List<TreePath> treePaths = TreeUtil.collectSelectedPaths(this.tree);
        for (TreePath treePath : treePaths) {
            Object pathComponent = treePath.getPathComponent(treePath.getPathCount() - 1);
            if (pathComponent instanceof MainTopTreeView.RequestMappingNode) {
                Controller controller = ((MainTopTreeView.RequestMappingNode) pathComponent).getData();
                result.add(controller);
            }
            if (pathComponent instanceof MainTopTreeView.ClassNameNode) {
                result.addAll(getControllerFormClassNameNode(((ClassNameNode) pathComponent)));
            }
            if (pathComponent instanceof MainTopTreeView.ProjectModuleNode) {
                MainTopTreeView.ProjectModuleNode projectModuleNode = (MainTopTreeView.ProjectModuleNode) pathComponent;
                for (int i = 0; i < projectModuleNode.getChildCount(); i++) {
                    javax.swing.tree.TreeNode child = projectModuleNode.getChildAt(i);
                    if (child instanceof ClassNameNode) {
                        result.addAll(getControllerFormClassNameNode(((ClassNameNode) child)));
                    }
                }
            }
            if (pathComponent instanceof FeaturesModuleNode) {
                FeaturesModuleNode controllerFeaturesModuleNode = (FeaturesModuleNode) pathComponent;
                if (controllerFeaturesModuleNode.getData().equalsIgnoreCase("controller")) {
                    UserProjectManager userProjectManager = UserProjectManager.getInstance(project);
                    result.addAll(userProjectManager.getController());
                }
            }
        }
        return result;
    }

    private List<Controller> getControllerFormClassNameNode(MainTopTreeView.ClassNameNode classNameNode) {
        return getControllerFormClassNameNode(classNameNode, new ArrayList<>());
    }

    private List<Controller> getControllerFormClassNameNode(MainTopTreeView.ClassNameNode classNameNode, List<Controller> result) {
        for (int i = 0; i < classNameNode.getChildCount(); i++) {
            javax.swing.tree.TreeNode treeNode = classNameNode.getChildAt(i);
            if (treeNode instanceof RequestMappingNode) {
                result.add(((RequestMappingNode) treeNode).getData());
            }
            if (treeNode instanceof ClassNameNode) {
                getControllerFormClassNameNode(((ClassNameNode) treeNode), result);
            }
        }
        return result;
    }

    public FeaturesModuleNode getControllerFeaturesModuleNode() {
        return controllerFeaturesModuleNode;
    }

    public FeaturesModuleNode getScheduledFeaturesModuleNode() {
        return scheduledFeaturesModuleNode;
    }

    public RootNode getRoot() {
        return root;
    }

    public static class CustomControllerFolderNode extends TreeNode<CustomControllerFolderPersistent.Folder> implements CanDelete {
        public CustomControllerFolderNode(CustomControllerFolderPersistent.Folder data) {
            super(data);
        }
    }

    /**
     * 用户项目模块
     */
    public static class ProjectModuleNode extends TreeNode<String> {
        public ProjectModuleNode(String data) {
            super(data);
        }
    }

    /**
     * 类名
     */
    public static class ClassNameNode extends TreeNode<String> {

        public ClassNameNode(String data) {
            super(data);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }

    public static class PackageNameNode extends ClassNameNode {
        public PackageNameNode(String data) {
            super(data);
        }
    }

    /**
     * 调度器
     */
    public static class BasicScheduledMethodNode<T> extends TreeNode<T> {
        public BasicScheduledMethodNode(T data) {
            super(data);
        }

    }

    public static class SpringScheduledMethodNode extends BasicScheduledMethodNode<SpringScheduled> {
        public SpringScheduledMethodNode(SpringScheduled data) {
            super(data);
        }

    }

    public static class XxlJobMethodNode extends BasicScheduledMethodNode<XxlJobScheduled> {
        public XxlJobMethodNode(XxlJobScheduled data) {
            super(data);
        }

    }


    /**
     * 请求方法信息
     */
    public static class RequestMappingNode extends TreeNode<Controller> {
        public RequestMappingNode(Controller data) {
            super(data);
        }
    }

    /**
     * 请求方法信息
     */
    public static class CustomMappingNode extends RequestMappingNode implements CanDelete {
        public CustomMappingNode(Controller data) {
            super(data);
        }
    }

    /**
     * 模块
     */
    public static class FeaturesModuleNode extends TreeNode<String> {
        public FeaturesModuleNode(String data) {
            super(data);
        }
    }

    /**
     * root
     */
    public static class RootNode extends TreeNode<String> {
        public RootNode(String data) {
            super(data);
        }
    }

    public static abstract class TreeNode<T> extends DefaultMutableTreeNode {
        public TreeNode(T data) {
            super(data);
        }

        public T getData() {
            return (T) getUserObject();
        }
    }

    public Project getProject() {
        return project;
    }
}
