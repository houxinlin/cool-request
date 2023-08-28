package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.action.ApifoxExportAnAction;
import com.hxl.plugin.springboot.invoke.action.CleanCacheAnAction;
import com.hxl.plugin.springboot.invoke.listener.EndpointListener;
import com.hxl.plugin.springboot.invoke.listener.RequestMappingSelectedListener;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingInvokeBean;
import com.hxl.plugin.springboot.invoke.model.SpringScheduledInvokeBean;
import com.hxl.plugin.springboot.invoke.view.PluginWindowView;
import com.hxl.plugin.springboot.invoke.view.RestfulTreeCellRenderer;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.treeStructure.actions.CollapseAllAction;
import com.intellij.ui.treeStructure.actions.ExpandAllAction;
import com.intellij.util.ui.tree.TreeUtil;

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

public class MainTopTreeView extends JPanel implements EndpointListener  {
    private final Tree tree = new SimpleTree();
    private final Project project;
    private final List<RequestMappingSelectedListener> requestMappingSelectedListeners = new ArrayList<>();
    private final Map<ClassNameNode, List<RequestMappingNode>> requestMappingNodeMap = new HashMap<>();//类名节点->所有实例节点
    private final Map<ClassNameNode, List<ScheduledMethodNode>> scheduleMapNodeMap = new HashMap<>();//类名节点->所有实例节点
    private  final RootNode root = new RootNode(( " mapper"));
    private final ModuleNode controllerModuleNode = new ModuleNode("Controller");
    private final ModuleNode scheduledModuleNode = new ModuleNode("Scheduled");
    private final JProgressBar jProgressBar =new JProgressBar();

    public MainTopTreeView(Project project, PluginWindowView pluginWindowView) {
        this.project = project;
        this.setLayout(new BorderLayout());

        JPanel progressJpanel = new JPanel(new BorderLayout());
        progressJpanel.add(jProgressBar);
        TreeUtil.installActions(tree);
        ((SimpleTree) tree).setPopupGroup(getPopupActions(),"");

        //设置点击事件
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (lastSelectedPathComponent == null) return;
            Object userObject = lastSelectedPathComponent.getUserObject();
            if (userObject instanceof RequestMappingModel) {
                for (RequestMappingSelectedListener requestMappingSelectedListener : requestMappingSelectedListeners) {
                    RequestMappingModel requestMappingModel = (RequestMappingModel) userObject;
                    navigate(requestMappingModel);
                    requestMappingSelectedListener.controllerChooseEvent(requestMappingModel);
                }
            }
            if (userObject instanceof SpringScheduledInvokeBean) {
                for (RequestMappingSelectedListener requestMappingSelectedListener : requestMappingSelectedListeners) {
                    SpringScheduledInvokeBean springScheduledInvokeBean = (SpringScheduledInvokeBean) userObject;
                    navigate(springScheduledInvokeBean);
                    requestMappingSelectedListener.scheduledChooseEvent(springScheduledInvokeBean);
                }
            }
            pluginWindowView.repaint();
            pluginWindowView.invalidate();
        });
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(new DefaultMutableTreeNode());
        tree.setCellRenderer(new RestfulTreeCellRenderer());
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);

        JBScrollPane mainJBScrollPane = new JBScrollPane();
        mainJBScrollPane.setViewportView(tree);
        progressJpanel.setOpaque(false);
        jProgressBar.setValue(0);
        this.add(mainJBScrollPane,BorderLayout.CENTER);
        this.add(progressJpanel,BorderLayout.NORTH);
        root.add(controllerModuleNode);
        root.add(scheduledModuleNode);

        ((DefaultTreeModel) tree.getModel()).setRoot(root);
    }

    protected ActionGroup getPopupActions() {
        DefaultActionGroup subMenu = new DefaultActionGroup("export", true);
        subMenu.add(new ApifoxExportAnAction(((SimpleTree) this.tree)));

        DefaultActionGroup group = new DefaultActionGroup();
        group.add(subMenu);
        group.addSeparator();
        group.add(new CleanCacheAnAction());
        group.addSeparator();
        group.add(new ExpandAllAction(tree));
        group.add(new CollapseAllAction(tree));

        return group;
    }
    public void selectNode( ScheduledMethodNode value) {
        for (ClassNameNode classNameNode : scheduleMapNodeMap.keySet()) {
            for (ScheduledMethodNode scheduledMethodNode : scheduleMapNodeMap.get(classNameNode)) {
                if (scheduledMethodNode==value){
                    TreePath path = new TreePath(new Object[]{root, scheduledModuleNode,classNameNode, value});
                    tree.getSelectionModel().setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                    tree.updateUI();
                }
            }
        }
    }

    public void selectNode( RequestMappingNode requestMappingNode) {
        for (ClassNameNode classNameNode : requestMappingNodeMap.keySet()) {
            for (RequestMappingNode value :requestMappingNodeMap.get(classNameNode)) {
                if (value ==requestMappingNode){
                    TreePath path = new TreePath(new Object[]{root, controllerModuleNode,classNameNode, requestMappingNode});
                    tree.getSelectionModel().setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                    tree.updateUI();
                }
            }
        }
    }

    @Override
    public void clear() {
        controllerModuleNode.removeAllChildren();
        scheduledModuleNode.removeAllChildren();
        requestMappingNodeMap.clear();
        scheduleMapNodeMap.clear();
    }

    private ClassNameNode getExistClassNameNode(SpringScheduledInvokeBean scheduledInvokeBean){
        for (ClassNameNode classNameNode : requestMappingNodeMap.keySet()) {
            if (classNameNode.getData().equalsIgnoreCase(scheduledInvokeBean.getClassName())) return classNameNode;
        }
        return null;
    }

    @Override
    public void onEndpoint(List<SpringScheduledInvokeBean> springMvcRequestMappingInvokeBean) {
        scheduledModuleNode.removeAllChildren();
        for (SpringScheduledInvokeBean springScheduledInvokeBean : springMvcRequestMappingInvokeBean) {
            ClassNameNode moduleNode =getExistClassNameNode(springScheduledInvokeBean);//添加到现有的里面
            if (scheduleMapNodeMap.values().stream().anyMatch(scheduledMethodNodes -> {
                for (ScheduledMethodNode requestMappingNode : scheduledMethodNodes) {
                    if (requestMappingNode.getData().getId().equalsIgnoreCase(springScheduledInvokeBean.getId()))
                        return true;
                }
                return false;
            })){
                return;
            }
            if (moduleNode ==null){
                moduleNode =new ClassNameNode(springScheduledInvokeBean.getClassName());
                scheduledModuleNode.add(moduleNode);
                scheduleMapNodeMap.put(moduleNode,new ArrayList<>());
            }
            ScheduledMethodNode scheduledMethodNode = new ScheduledMethodNode(springScheduledInvokeBean);
            scheduleMapNodeMap.get(moduleNode).add(scheduledMethodNode);
            moduleNode.add(scheduledMethodNode);
        }
    }

    private ClassNameNode getExistClassNameNode(RequestMappingModel requestMappingModel){
        for (ClassNameNode classNameNode : requestMappingNodeMap.keySet()) {
            if (classNameNode.getData().equalsIgnoreCase(requestMappingModel.getController().getSimpleClassName())) return classNameNode;
        }
        return null;
    }
    @Override
    public void onEndpoint(RequestMappingModel requestMappingModel) {
        SpringMvcRequestMappingInvokeBean requestMappingInvokeBean = requestMappingModel.getController();
        float current =requestMappingModel.getCurrent();
        float total =requestMappingModel.getTotal();
        int i = new BigDecimal(current).divide(new BigDecimal(total), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100)).intValue();
        jProgressBar.setVisible(i!=100);
        jProgressBar.setValue(i);
        ClassNameNode moduleNode =getExistClassNameNode(requestMappingModel);//添加到现有的里面
        if (requestMappingNodeMap.values().stream().anyMatch(requestMappingNodes -> {
            for (RequestMappingNode requestMappingNode : requestMappingNodes) {
                if (requestMappingNode.getData().getController().getId().equalsIgnoreCase(requestMappingModel.getController().getId()))
                    return true;
            }
            return false;
        })){
            return;
        }
        if (moduleNode ==null){
            moduleNode =new ClassNameNode(requestMappingInvokeBean.getSimpleClassName());
            controllerModuleNode.add(moduleNode);
            requestMappingNodeMap.put(moduleNode,new ArrayList<>());
        }
        RequestMappingNode requestMappingNode = new RequestMappingNode(requestMappingModel);
        requestMappingNodeMap.get(moduleNode).add(requestMappingNode);
        moduleNode.add(requestMappingNode);

    }


    private void navigate(RequestMappingModel requestMappingModel) {
        PsiClass psiClass = findClassByName(project, requestMappingModel.getController().getSimpleClassName());
        if (psiClass != null) {
            PsiMethod methodInClass = findMethodInClass(psiClass, requestMappingModel.getController().getMethodName());
            if (methodInClass != null) methodInClass.navigate(true);
        }
    }
    private void navigate(SpringScheduledInvokeBean requestMappingModel) {
        PsiClass psiClass = findClassByName(project, requestMappingModel.getClassName());
        if (psiClass != null) {
            PsiMethod methodInClass = findMethodInClass(psiClass, requestMappingModel.getMethodName());
            if (methodInClass != null) methodInClass.navigate(true);
        }
    }
    public void registerRequestMappingSelected(RequestMappingSelectedListener requestMappingSelectedListener) {
        this.requestMappingSelectedListeners.add(requestMappingSelectedListener);
    }

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
    public static class ScheduledMethodNode extends TreeNode<SpringScheduledInvokeBean> {
        public ScheduledMethodNode(SpringScheduledInvokeBean data) {
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
        private final T data;

        public TreeNode(T data) {
            super(data);
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }
}
