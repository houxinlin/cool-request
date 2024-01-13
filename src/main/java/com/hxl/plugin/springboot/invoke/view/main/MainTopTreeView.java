package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.action.controller.CollapseSelectedAction;
import com.hxl.plugin.springboot.invoke.action.controller.ExpandSelectedAction;
import com.hxl.plugin.springboot.invoke.action.export.ApifoxExportAnAction;
import com.hxl.plugin.springboot.invoke.action.CleanCacheAnAction;
import com.hxl.plugin.springboot.invoke.action.export.OpenApiExportAnAction;
import com.hxl.plugin.springboot.invoke.action.copy.*;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.bean.components.scheduled.SpringScheduled;
import com.hxl.plugin.springboot.invoke.model.ScheduledModel;
import com.hxl.plugin.springboot.invoke.model.SpringScheduledSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.state.SettingPersistentState;
import com.hxl.plugin.springboot.invoke.state.SettingsState;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.hxl.plugin.springboot.invoke.utils.SpringScheduledSpringInvokeEndpointWrapper;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.hxl.plugin.springboot.invoke.view.RestfulTreeCellRenderer;
import com.intellij.openapi.actionSystem.*;
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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

import static com.hxl.plugin.springboot.invoke.utils.PsiUtils.*;

public class MainTopTreeView extends JPanel {
    private final Tree tree = new SimpleTree();
    private final Project project;
    private final Map<ClassNameNode, List<RequestMappingNode>> requestMappingNodeMap = new HashMap<>();//类名节点->所有实例节点
    private final Map<ClassNameNode, List<ScheduledMethodNode>> scheduleMapNodeMap = new HashMap<>();//类名节点->所有实例节点
    private final RootNode root = new RootNode(("0 mapper"));
    private final FeaturesModuleNode controllerFeaturesModuleNode = new FeaturesModuleNode("Controller");
    private final FeaturesModuleNode scheduledFeaturesModuleNode = new FeaturesModuleNode("Scheduled");
    private final List<ProjectModuleNode> projectModuleNodes = new ArrayList<>();
    private final JProgressBar jProgressBar = new JProgressBar();
    private final DefaultActionGroup exportActionGroup = new DefaultActionGroup("Export", true);
    private final DefaultActionGroup copyActionGroup = new DefaultActionGroup("Copy", true);
    //    private final DefaultActionGroup configActionGroup = new DefaultActionGroup("Config", true);
    private final List<String> EXCLUDE_CLASS_NAME = Arrays.asList("org.springframework.boot.autoconfigure.web.servlet", "org.springdoc.webmvc");

    private TreeNode currentTreeNode;

    private boolean isSelected(TreePath path) {
        TreePath[] selectionPaths = tree.getSelectionPaths();
        return selectionPaths != null && ArrayUtil.contains(path, selectionPaths);
    }

    public TreeNode getCurrentTreeNode() {
        return currentTreeNode;
    }

    protected void invokeContextMenu(@NotNull final MouseEvent e, @NotNull ActionGroup actionGroup) {
        JPopupMenu component = ActionManager.getInstance().createActionPopupMenu("right", actionGroup).getComponent();
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
            if (lastSelectedPathComponent instanceof TreeNode) {
                currentTreeNode = ((TreeNode<?>) lastSelectedPathComponent);
            }
            if (userObject instanceof Controller) {
                Controller controller = (Controller) userObject;
                navigate(controller);
                project.getMessageBus().syncPublisher(IdeaTopic.CONTROLLER_CHOOSE_EVENT).onChooseEvent(controller);
            }
            if (userObject instanceof SpringScheduled) {
                SpringScheduled springScheduled = (SpringScheduled) userObject;
                navigate(springScheduled);
                project.getMessageBus().syncPublisher(IdeaTopic.SCHEDULED_CHOOSE_EVENT)
                        .onChooseEvent(springScheduled);

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
        root.add(controllerFeaturesModuleNode);
        root.add(scheduledFeaturesModuleNode);

        exportActionGroup.add(new ApifoxExportAnAction(this));
//        subMenu.add(new ApipostExportAnAction((this)));
        // TODO: 2023/9/23 目前找到不到接口
        exportActionGroup.add(new OpenApiExportAnAction((this)));

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

        connect.subscribe(IdeaTopic.ADD_SPRING_SCHEDULED_MODEL, (IdeaTopic.SpringScheduledModel) scheduledModel ->
                SwingUtilities.invokeLater(() -> addScheduled(scheduledModel)));

        connect.subscribe(IdeaTopic.ADD_SPRING_REQUEST_MAPPING_MODEL, new IdeaTopic.SpringRequestMappingModel() {
            @Override
            public void addRequestMappingModel(List<? extends Controller> controllers) {
                addController(controllers);
            }
        });

        connect.subscribe(IdeaTopic.DELETE_ALL_DATA, (IdeaTopic.DeleteAllDataEventListener) () -> clearData());
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
        group.add(new ExpandSelectedAction(tree));
        group.add(new CollapseSelectedAction(tree));
        return group;
    }

    public void selectNode(TreeNode node) {
        if (node == null) return;
        SwingUtilities.invokeLater(() -> {
            TreePath treePath = new TreePath(node.getPath());
            tree.getSelectionModel().setSelectionPath(treePath);
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
                    for (List<MainTopTreeView.RequestMappingNode> value : getRequestMappingNodeMap().values()) {
                        for (MainTopTreeView.RequestMappingNode requestMappingNode : value) {
                            result.add(requestMappingNode.getData());
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<Controller> getControllerFormClassNameNode(MainTopTreeView.ClassNameNode classNameNode) {
        List<Controller> result = new ArrayList<>();
        List<MainTopTreeView.RequestMappingNode> requestMappingNodes = getRequestMappingNodeMap().get(classNameNode);
        for (MainTopTreeView.RequestMappingNode requestMappingNode : requestMappingNodes) {
            result.add(requestMappingNode.getData());
        }
        return result;
    }

    public void clearData() {
        controllerFeaturesModuleNode.removeAllChildren();
        scheduledFeaturesModuleNode.removeAllChildren();
        requestMappingNodeMap.clear();
        scheduleMapNodeMap.clear();
        root.setUserObject("0 mapper");
        SwingUtilities.invokeLater(MainTopTreeView.this.tree::updateUI);
    }


    private <T> ClassNameNode getExistClassNameNode(ProjectModuleNode projectModuleNode, String className, Map<ClassNameNode, List<T>> targetNodeMap) {
        for (int i = 0; i < projectModuleNode.getChildCount(); i++) {
            javax.swing.tree.TreeNode node = projectModuleNode.getChildAt(i);
            if (node instanceof ClassNameNode) {
                boolean equals = ((ClassNameNode) node).getData().equals(className);
                if (equals) return ((ClassNameNode) node);
            }
        }

        ClassNameNode classNameNode = new ClassNameNode(className);
        projectModuleNode.add(classNameNode);
        targetNodeMap.put(classNameNode, new ArrayList<>());
        return classNameNode;
    }

    private boolean isExist(Controller controller) {
        String id = controller.getId();
        return requestMappingNodeMap.values().stream().anyMatch(requestMappingNodes -> {
            for (RequestMappingNode requestMappingNode : requestMappingNodes) {
                if (requestMappingNode.getData().getId().equalsIgnoreCase(id)) {
                    requestMappingNode.setUserObject(controller);
                    return true;
                }
            }
            return false;
        });
    }

    private boolean isExist(SpringScheduled scheduled) {
        String id = scheduled.getId();
        return scheduleMapNodeMap.values().stream().anyMatch(scheduledMethodNodes -> {
            for (ScheduledMethodNode requestMappingNode : scheduledMethodNodes) {
                if (requestMappingNode.getData().getId().equalsIgnoreCase(id)) {
                    requestMappingNode.setUserObject(scheduled);
                    return true;
                }
            }
            return false;
        });
    }

    private boolean isFilter(Controller controller) {
        for (String className : EXCLUDE_CLASS_NAME) {
            if (controller.getSimpleClassName().startsWith(className)) return true;
        }
        return false;
    }

    private ProjectModuleNode getExistProjectModule(String moduleName, FeaturesModuleNode targetFeaturesModuleNode) {
        for (int i = 0; i < targetFeaturesModuleNode.getChildCount(); i++) {
            javax.swing.tree.TreeNode node = targetFeaturesModuleNode.getChildAt(i);
            if (node instanceof ProjectModuleNode) {
                boolean equals = ((ProjectModuleNode) node).getData().equals(moduleName);
                if (equals) return ((ProjectModuleNode) node);
            }
        }
        ProjectModuleNode projectModuleNode = new ProjectModuleNode(moduleName);
        targetFeaturesModuleNode.add(projectModuleNode);
        return projectModuleNode;
    }

    private void addController(List<? extends Controller> controllers) {
        if (controllers == null || controllers.isEmpty()) {
            return;
        }
        for (Controller controller : controllers) {
            if (isFilter(controller)) continue;
            if (isExist(controller)) continue;
            ProjectModuleNode projectModuleNode = getExistProjectModule(controller.getModuleName(), controllerFeaturesModuleNode);
            ClassNameNode classNameNode = getExistClassNameNode(projectModuleNode, controller.getSimpleClassName(), requestMappingNodeMap);
            RequestMappingNode requestMappingNode = new RequestMappingNode(controller);
            requestMappingNodeMap.get(classNameNode).add(requestMappingNode);

            classNameNode.add(requestMappingNode);
        }
        root.setUserObject(getControllerCount() + " mapper");
        SwingUtilities.invokeLater(() -> {
            tree.updateUI();
        });

    }

    public void addScheduled(List<? extends SpringScheduled> scheduled) {
        if (scheduled == null || scheduled.isEmpty()) {
            return;
        }
        for (SpringScheduled springScheduled : scheduled) {
            if (isExist(springScheduled)) continue;
            ProjectModuleNode projectModuleNode = getExistProjectModule(springScheduled.getModuleName(), scheduledFeaturesModuleNode);
            ClassNameNode classNameNode = getExistClassNameNode(projectModuleNode, springScheduled.getClassName(), scheduleMapNodeMap);
            ScheduledMethodNode requestMappingNode = new ScheduledMethodNode(springScheduled);
            scheduleMapNodeMap.get(classNameNode).add(requestMappingNode);
            classNameNode.add(requestMappingNode);
        }
        SwingUtilities.invokeLater(() -> {
            tree.updateUI();
        });
    }

    private int getControllerCount() {
        int result = 0;
        for (List<RequestMappingNode> value : requestMappingNodeMap.values()) {
            result += value.size();
        }
        return result;
    }

    private void navigate(Controller controller) {
        PsiClass psiClass = findClassByName(project, controller.getModuleName(), controller.getSimpleClassName());
        if (psiClass != null) {
            PsiMethod httpMethodMethodInClass = findHttpMethodInClass(psiClass,
                    controller.getMethodName(),
                    controller.getHttpMethod(),
                    controller.getParamClassList(), controller.getUrl());
            if (httpMethodMethodInClass != null) navigateFilter(httpMethodMethodInClass);
        }
    }

    private void navigateFilter(PsiMethod psiMethod) {
        if (!SettingPersistentState.getInstance().getState().autoNavigation) return;
        PsiUtils.methodNavigate(psiMethod);
    }

    private void navigate(SpringScheduled springScheduled) {
        PsiClass psiClass = findClassByName(project, springScheduled.getModuleName(), springScheduled.getClassName());
        if (psiClass != null) {
            PsiMethod methodInClass = findMethodInClassOne(psiClass, springScheduled.getMethodName());
            if (methodInClass != null) navigateFilter(methodInClass);
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
    }

    /**
     * 调度器
     */
    public static class ScheduledMethodNode extends TreeNode<SpringScheduled> {
        public ScheduledMethodNode(SpringScheduled data) {
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
