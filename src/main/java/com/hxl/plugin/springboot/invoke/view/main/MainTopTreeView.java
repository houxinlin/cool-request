package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.action.ApifoxExportAnAction;
import com.hxl.plugin.springboot.invoke.action.CleanCacheAnAction;
import com.hxl.plugin.springboot.invoke.action.OpenApiAnAction;
import com.hxl.plugin.springboot.invoke.action.copy.*;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.ScheduledModel;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.model.SpringScheduledSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.utils.EmptyProgressTask;
import com.hxl.plugin.springboot.invoke.utils.SpringScheduledSpringInvokeEndpointWrapper;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.hxl.plugin.springboot.invoke.view.RestfulTreeCellRenderer;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.treeStructure.actions.CollapseAllAction;
import com.intellij.ui.treeStructure.actions.ExpandAllAction;
import com.intellij.util.ArrayUtil;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;

import static com.hxl.plugin.springboot.invoke.utils.PsiUtils.*;

public class MainTopTreeView extends JPanel {
    private final Tree tree = new SimpleTree();
    private final Project project;
    private final Map<ClassNameNode, List<RequestMappingNode>> requestMappingNodeMap = new HashMap<>();//类名节点->所有实例节点
    private final Map<ClassNameNode, List<ScheduledMethodNode>> scheduleMapNodeMap = new HashMap<>();//类名节点->所有实例节点
    private final RootNode root = new RootNode(("0 mapper"));
    private final ModuleNode controllerModuleNode = new ModuleNode("Controller");
    private final ModuleNode scheduledModuleNode = new ModuleNode("Scheduled");
    private final JProgressBar jProgressBar = new JProgressBar();
    private final DefaultActionGroup exportActionGroup = new DefaultActionGroup("Export", true);
    private final DefaultActionGroup copyActionGroup = new DefaultActionGroup("Copy", true);
//    private final DefaultActionGroup configActionGroup = new DefaultActionGroup("Config", true);
    private final List<String> EXCLUDE_CLASS_NAME = Arrays.asList("org.springframework.boot.autoconfigure.web.servlet", "org.springdoc.webmvc");

    private boolean isSelected(TreePath path) {
        TreePath[] selectionPaths = tree.getSelectionPaths();
        return selectionPaths != null && ArrayUtil.contains(path, selectionPaths);
    }

    protected void invokeContextMenu(@NotNull final MouseEvent e, @NotNull ActionGroup actionGroup) {
        JPopupMenu component = ActionManager.getInstance().createActionPopupMenu("", actionGroup).getComponent();
        component.show(e.getComponent(), e.getX(), e.getY());
    }

    public MainTopTreeView(Project project, CoolIdeaPluginWindowView coolIdeaPluginWindowView) {
        this.project = project;
        this.setLayout(new BorderLayout());

        JPanel progressJpanel = new JPanel(new BorderLayout());
//        progressJpanel.add(jProgressBar);
        TreeUtil.installActions(tree);
//        ((SimpleTree) tree).setPopupGroup(getPopupActions(), "");
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
                invokePopup(e);
            }

            private void invokePopup(final MouseEvent e) {
                if (e.isPopupTrigger() && insideTreeItemsArea(e)) {
                    TreePath pathForLocation = selectPathUnderCursorIfNeeded(e);
                    if (pathForLocation != null && pathForLocation.getLastPathComponent() instanceof RequestMappingNode) {
                        invokeContextMenu(e, getPopupActions(exportActionGroup, copyActionGroup));
                    } else {
                        invokeContextMenu(e, getPopupActions(exportActionGroup));
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
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (lastSelectedPathComponent == null) return;
            Object userObject = lastSelectedPathComponent.getUserObject();
            if (userObject instanceof RequestMappingModel) {
                RequestMappingModel requestMappingModel = (RequestMappingModel) userObject;
                navigate(requestMappingModel);
                project.getMessageBus().syncPublisher(IdeaTopic.CONTROLLER_CHOOSE_EVENT).onChooseEvent(requestMappingModel);
            }
            if (userObject instanceof SpringScheduledSpringInvokeEndpointWrapper) {
                SpringScheduledSpringInvokeEndpointWrapper springScheduledSpringInvokeEndpoint = (SpringScheduledSpringInvokeEndpointWrapper) userObject;
                navigate(springScheduledSpringInvokeEndpoint);
                project.getMessageBus().syncPublisher(IdeaTopic.SCHEDULED_CHOOSE_EVENT)
                        .onChooseEvent(springScheduledSpringInvokeEndpoint.getSpringScheduledSpringInvokeEndpoint(), springScheduledSpringInvokeEndpoint.getPort());

            }
            coolIdeaPluginWindowView.repaint();
            coolIdeaPluginWindowView.invalidate();
        });
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(new DefaultMutableTreeNode());
        tree.setCellRenderer(new RestfulTreeCellRenderer());
        tree.setRootVisible(true);
        tree.setShowsRootHandles(false);
        JBScrollPane mainJBScrollPane = new JBScrollPane();
        mainJBScrollPane.setViewportView(tree);
        progressJpanel.setOpaque(false);
        jProgressBar.setValue(0);
        this.add(mainJBScrollPane, BorderLayout.CENTER);
        this.add(progressJpanel, BorderLayout.NORTH);
        root.add(controllerModuleNode);
        root.add(scheduledModuleNode);

        exportActionGroup.add(new ApifoxExportAnAction(this));
//        subMenu.add(new ApipostExportAnAction((this)));
        // TODO: 2023/9/23 目前找到不到接口
        exportActionGroup.add(new OpenApiAnAction((this)));

        copyActionGroup.add(new CopyClassNameAnAction(this));
        copyActionGroup.add(new CopyCurlAnAction(this));
        copyActionGroup.add(new CopyHttpUrlAnAction(this));
        copyActionGroup.add(new CopyMethodNameAnAction(this));
        copyActionGroup.add(new CopyOpenApiAction(this));

//        tree.addTreeExpansionListener(new TreeExpansionListener() {
//            @Override
//            public void treeExpanded(TreeExpansionEvent event) {
//                Object lastPathComponent = event.getPath().getLastPathComponent();
//
//                TreePath path = event.getPath();
//                System.out.println(path);
//            }
//
//            @Override
//            public void treeCollapsed(TreeExpansionEvent event) {
//
//            }
//        });
        MessageBusConnection connect = project.getMessageBus().connect();

        connect.subscribe(IdeaTopic.ADD_SPRING_SCHEDULED_MODEL, scheduledModel -> SwingUtilities.invokeLater(() -> onEndpoint(scheduledModel)));

        connect.subscribe(IdeaTopic.ADD_SPRING_REQUEST_MAPPING_MODEL, springInvokeEndpoint -> SwingUtilities.invokeLater(() -> onEndpoint(springInvokeEndpoint)));

        ((DefaultTreeModel) tree.getModel()).setRoot(root);
    }

    public Tree getTree() {
        return tree;
    }


    protected ActionGroup getPopupActions(AnAction... actions) {
        DefaultActionGroup group = new DefaultActionGroup();
        for (AnAction action : actions) {
            group.add(action);
        }
        group.addSeparator();
        group.add(new CleanCacheAnAction(this));
        group.addSeparator();
        group.add(new ExpandAllAction(tree));
        group.add(new CollapseAllAction(tree));
        return group;
    }

    public void selectNode(ScheduledMethodNode value) {
        for (ClassNameNode classNameNode : scheduleMapNodeMap.keySet()) {
            for (ScheduledMethodNode scheduledMethodNode : scheduleMapNodeMap.get(classNameNode)) {
                if (scheduledMethodNode == value) {
                    TreePath path = new TreePath(new Object[]{root, scheduledModuleNode, classNameNode, value});
                    tree.getSelectionModel().setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                    SwingUtilities.invokeLater(tree::updateUI);
                }
            }
        }
    }

    public List<RequestMappingModel> getSelectRequestMappings() {
        List<RequestMappingModel> requestMappingModels = new ArrayList<>();
        List<TreePath> treePaths = TreeUtil.collectSelectedPaths(this.tree);
        for (TreePath treePath : treePaths) {
            Object pathComponent = treePath.getPathComponent(treePath.getPathCount() - 1);
            if (pathComponent instanceof MainTopTreeView.RequestMappingNode) {
                RequestMappingModel requestMappingModel = ((MainTopTreeView.RequestMappingNode) pathComponent).getData();
                requestMappingModels.add(requestMappingModel);
            }
            if (pathComponent instanceof MainTopTreeView.ClassNameNode) {
                MainTopTreeView.ClassNameNode classNameNode = (MainTopTreeView.ClassNameNode) pathComponent;
                List<MainTopTreeView.RequestMappingNode> requestMappingNodes = getRequestMappingNodeMap().get(classNameNode);
                for (MainTopTreeView.RequestMappingNode requestMappingNode : requestMappingNodes) {
                    requestMappingModels.add(requestMappingNode.getData());
                }
            }
            if (pathComponent instanceof MainTopTreeView.ModuleNode) {
                MainTopTreeView.ModuleNode controllerModuleNode = (MainTopTreeView.ModuleNode) pathComponent;
                if (controllerModuleNode.getData().equalsIgnoreCase("controller")) {
                    for (List<MainTopTreeView.RequestMappingNode> value : getRequestMappingNodeMap().values()) {
                        for (MainTopTreeView.RequestMappingNode requestMappingNode : value) {
                            requestMappingModels.add(requestMappingNode.getData());
                        }
                    }
                }
            }
        }
        return requestMappingModels;
    }

    public void selectNode(RequestMappingNode requestMappingNode) {
        for (ClassNameNode classNameNode : requestMappingNodeMap.keySet()) {
            for (RequestMappingNode value : requestMappingNodeMap.get(classNameNode)) {
                if (value == requestMappingNode) {
                    TreePath path = new TreePath(new Object[]{root, controllerModuleNode, classNameNode, requestMappingNode});
                    tree.getSelectionModel().setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                    SwingUtilities.invokeLater(tree::updateUI);
                }
            }
        }
    }

    public void clear() {
        controllerModuleNode.removeAllChildren();
        scheduledModuleNode.removeAllChildren();
        requestMappingNodeMap.clear();
        scheduleMapNodeMap.clear();
        root.setUserObject("0 mapper");
        SwingUtilities.invokeLater(MainTopTreeView.this.tree::updateUI);
    }

    private ClassNameNode getExistClassNameNode(SpringScheduledSpringInvokeEndpoint scheduledInvokeBean) {
        for (ClassNameNode classNameNode : requestMappingNodeMap.keySet()) {
            if (classNameNode.getData().equalsIgnoreCase(scheduledInvokeBean.getClassName())) return classNameNode;
        }
        return null;
    }

    public void onEndpoint(ScheduledModel scheduledModel) {
        scheduledModuleNode.removeAllChildren();
        for (SpringScheduledSpringInvokeEndpoint SpringScheduledSpringInvokeEndpoint : scheduledModel.getScheduledInvokeBeans()) {
            ClassNameNode moduleNode = getExistClassNameNode(SpringScheduledSpringInvokeEndpoint);//添加到现有的里面
            if (scheduleMapNodeMap.values().stream().anyMatch(scheduledMethodNodes -> {
                for (ScheduledMethodNode requestMappingNode : scheduledMethodNodes) {
                    if (requestMappingNode.getData().getSpringScheduledSpringInvokeEndpoint()
                            .getId().equalsIgnoreCase(SpringScheduledSpringInvokeEndpoint.getId()))
                        return true;
                }
                return false;
            })) {
                return;
            }
            if (moduleNode == null) {
                moduleNode = new ClassNameNode(SpringScheduledSpringInvokeEndpoint.getClassName());
                scheduledModuleNode.add(moduleNode);
                scheduleMapNodeMap.put(moduleNode, new ArrayList<>());
            }
            ScheduledMethodNode scheduledMethodNode = new ScheduledMethodNode(new SpringScheduledSpringInvokeEndpointWrapper(SpringScheduledSpringInvokeEndpoint, scheduledModel.getPort()));
            scheduleMapNodeMap.get(moduleNode).add(scheduledMethodNode);
            moduleNode.add(scheduledMethodNode);
            root.setUserObject(getControllerCount() + " mapper");
            SwingUtilities.invokeLater(tree::updateUI);
        }
    }

    private ClassNameNode getExistClassNameNode(RequestMappingModel requestMappingModel) {
        for (ClassNameNode classNameNode : requestMappingNodeMap.keySet()) {
            if (classNameNode.getData().equalsIgnoreCase(requestMappingModel.getController().getSimpleClassName()))
                return classNameNode;
        }
        return null;
    }

    private boolean isExist(RequestMappingModel requestMappingModel) {
        String id = requestMappingModel.getController().getId();
        String url = requestMappingModel.getController().getUrl();
        return requestMappingNodeMap.values().stream().anyMatch(requestMappingNodes -> {
            for (RequestMappingNode requestMappingNode : requestMappingNodes) {
                if (requestMappingNode.getData().getController().getId()
                        .equalsIgnoreCase(id)) {
                    if (requestMappingNode.getData().getController().getUrl().equalsIgnoreCase(url)) {
                        requestMappingNode.setUserObject(requestMappingModel);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    public void onEndpoint(RequestMappingModel requestMappingModel) {
        if (requestMappingModel == null) {
            return;
        }
        //排除调spring的
        for (String className : EXCLUDE_CLASS_NAME) {
            if (requestMappingModel.getController().getSimpleClassName().startsWith(className)) return;
        }
        List<TreePath> treePaths = TreeUtil.collectExpandedPaths(this.tree);
        SpringMvcRequestMappingSpringInvokeEndpoint requestMappingInvokeBean = requestMappingModel.getController();
        float current = requestMappingModel.getCurrent();
        float total = requestMappingModel.getTotal();
        int i = new BigDecimal(current).divide(new BigDecimal(total), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100)).intValue();
        jProgressBar.setVisible(i != 100);
        jProgressBar.setValue(i);
        ClassNameNode moduleNode = getExistClassNameNode(requestMappingModel);//添加到现有的里面

        if (isExist(requestMappingModel)) return;

        if (moduleNode == null) {
            moduleNode = new ClassNameNode(requestMappingInvokeBean.getSimpleClassName());
            controllerModuleNode.add(moduleNode);
            requestMappingNodeMap.put(moduleNode, new ArrayList<>());
        }
        RequestMappingNode requestMappingNode = new RequestMappingNode(requestMappingModel);
        requestMappingNodeMap.get(moduleNode).add(requestMappingNode);
        moduleNode.add(requestMappingNode);
        root.setUserObject(getControllerCount() + " mapper");

        for (TreePath selectTreePath : treePaths) {
            tree.getSelectionModel().setSelectionPath(selectTreePath);
        }
        SwingUtilities.invokeLater(tree::updateUI);
        ProgressManager.getInstance().run(new EmptyProgressTask(project, "Refresh Controller"));
    }

    private int getControllerCount() {
        int result = 0;
        for (int i = 0; i < controllerModuleNode.getChildCount(); i++) {
            javax.swing.tree.TreeNode treeNode = controllerModuleNode.getChildAt(i);
            if (treeNode instanceof ClassNameNode) {
                result += treeNode.getChildCount();
            }
        }
        return result;
    }

    private void navigate(RequestMappingModel requestMappingModel) {
        PsiClass psiClass = findClassByName(project, requestMappingModel.getController().getSimpleClassName());
        if (psiClass != null) {
            PsiMethod httpMethodMethodInClass = findHttpMethodInClass(psiClass,
                    requestMappingModel.getController().getMethodName(),
                    requestMappingModel.getController().getHttpMethod(),
                    requestMappingModel.getController().getParamClassList(),requestMappingModel.getController().getUrl());

            if (httpMethodMethodInClass != null) httpMethodMethodInClass.navigate(true);
        }
    }

    private void navigate(SpringScheduledSpringInvokeEndpointWrapper requestMappingModel) {
        PsiClass psiClass = findClassByName(project, requestMappingModel.getSpringScheduledSpringInvokeEndpoint().getClassName());
        if (psiClass != null) {
            PsiMethod methodInClass = findMethodInClassOne(psiClass, requestMappingModel.getSpringScheduledSpringInvokeEndpoint().getMethodName());
            if (methodInClass != null) methodInClass.navigate(true);
        }
    }

//    public void registerRequestMappingSelected(SpringBootComponentSelectedListener springBootComponentSelectedListener) {
//        this.springBootComponentSelectedListeners.add(springBootComponentSelectedListener);
//    }

    public Map<ClassNameNode, List<RequestMappingNode>> getRequestMappingNodeMap() {
        return requestMappingNodeMap;
    }

    public Map<ClassNameNode, List<ScheduledMethodNode>> getScheduleMapNodeMap() {
        return scheduleMapNodeMap;
    }

    /**
     * 类名
     */
    public static class ClassNameNode extends TreeNode<String> {

        public ClassNameNode(String data) {
            super(data);
        }
    }

    /**
     * 调度器
     */
    public static class ScheduledMethodNode extends TreeNode<SpringScheduledSpringInvokeEndpointWrapper> {
        public ScheduledMethodNode(SpringScheduledSpringInvokeEndpointWrapper data) {
            super(data);
        }

    }

    /**
     * 请求方法信息
     */
    public static class RequestMappingNode extends TreeNode<RequestMappingModel> {
        public RequestMappingNode(RequestMappingModel data) {
            super(data);
        }
    }

    /**
     * 模块
     */
    public static class ModuleNode extends TreeNode<String> {
        public ModuleNode(String data) {
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
