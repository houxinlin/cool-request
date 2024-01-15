package com.hxl.plugin.springboot.invoke.action.actions;

import com.hxl.plugin.springboot.invoke.bean.components.BasicComponent;
import com.hxl.plugin.springboot.invoke.bean.components.controller.DynamicController;
import com.hxl.plugin.springboot.invoke.bean.components.controller.StaticController;
import com.hxl.plugin.springboot.invoke.bean.components.scheduled.SpringScheduled;
import com.hxl.plugin.springboot.invoke.tool.search.ApiAbstractGotoSEContributor;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.CoolIdeaPluginWindowView;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.treeStructure.Tree;
import com.jgoodies.common.base.Strings;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.*;

import static com.hxl.plugin.springboot.invoke.Constant.PLUGIN_ID;

/**
 * @author caoayu
 */
public class FindAction extends BaseAnAction {
    public FindAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("find"), () -> ResourceBundleUtils.getString("find"),
                AllIcons.Actions.Find);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        SearchEverywhereManager seManager = SearchEverywhereManager.getInstance(e.getProject());
        seManager.show(ApiAbstractGotoSEContributor.class.getSimpleName(), "", e);

//        Project project = e.getProject();
//        if (project == null) {
//            return;
//        }
//        String keyword = Messages.showInputDialog(
//                ResourceBundleUtils.getString("find.input"),
//                ResourceBundleUtils.getString("find"),
//                Messages.getQuestionIcon()
//        );
//        if (Strings.isBlank(keyword)) {
//            return;
//        }
//        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PLUGIN_ID);
//        assert toolWindow != null;
//        CoolIdeaPluginWindowView cool = (CoolIdeaPluginWindowView) Objects.requireNonNull(toolWindow.getContentManager().getContent(0)).getComponent();
//        MainTopTreeView mainTopTreeView = cool.getMainTopTreeView();
//        Tree tree = mainTopTreeView.getTree();
//        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
//        resetTree(tree, node);
//        expandNodesWithKeyword(node, keyword, mainTopTreeView);
    }
//
//    /**
//     * 重置树
//     * @param tree 树
//     * @param node 根节点
//     */
//    private void resetTree(Tree tree, DefaultMutableTreeNode node) {
//        Enumeration<TreeNode> children = node.children();
//        if (children.hasMoreElements()) {
//            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
//            // 清除所有展开状态
//            tree.collapsePath(new TreePath(childNode.getPath()));
//        }
//
//    }
//
//    /**
//     * 根据关键字展开节点
//     * @param node 根节点
//     * @param keyword 关键字
//     * @param mainTopTreeView 主界面
//     */
//    private void expandNodesWithKeyword(DefaultMutableTreeNode node, String keyword, MainTopTreeView mainTopTreeView) {
//        Tree tree = mainTopTreeView.getTree();
//        Enumeration<TreeNode> children = node.children();
//        Map<MainTopTreeView.ClassNameNode, List<MainTopTreeView.RequestMappingNode>> requestMappingNodeMap = mainTopTreeView.getRequestMappingNodeMap();
//        Map<MainTopTreeView.ClassNameNode, List<MainTopTreeView.ScheduledMethodNode>> scheduleMapNodeMap = mainTopTreeView.getScheduleMapNodeMap();
//
//        while (children.hasMoreElements()) {
//            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
//            // 递归展开子节点
//            expandNodesWithKeyword(childNode, keyword, mainTopTreeView);
//            if (!(childNode instanceof MainTopTreeView.ClassNameNode)) {
//                continue;
//            }
//            MainTopTreeView.ClassNameNode classNameNode = (MainTopTreeView.ClassNameNode) childNode;
//            List<MainTopTreeView.RequestMappingNode> requestMappingNodes = requestMappingNodeMap.getOrDefault(classNameNode, Collections.emptyList());
//            List<MainTopTreeView.ScheduledMethodNode> scheduledMethodNodes =
//                    scheduleMapNodeMap.getOrDefault(classNameNode, Collections.emptyList());
//            expandControllerNodes(childNode, keyword, requestMappingNodes, scheduledMethodNodes, tree);
//
//        }
//
//    }
//
//    /**
//     * 展开树节点
//     * @param childNode 子节点
//     * @param keyword 关键字
//     * @param requestMappingNodes 请求映射节点
//     * @param scheduledMethodNodes 定时任务节点
//     * @param tree 树
//     */
//    private void expandControllerNodes(DefaultMutableTreeNode childNode, String keyword,
//                                       List<MainTopTreeView.RequestMappingNode> requestMappingNodes,
//                                       List<MainTopTreeView.ScheduledMethodNode> scheduledMethodNodes,
//                                       Tree tree) {
//        for (MainTopTreeView.RequestMappingNode requestMappingNode : requestMappingNodes) {
//            if (expandControllerData(keyword, requestMappingNode.getData())) {
//                tree.expandPath(new TreePath(childNode.getPath()));
//            }
//        }
//
//        for (MainTopTreeView.ScheduledMethodNode scheduledMethodNode : scheduledMethodNodes) {
//            if (expandControllerData(keyword, scheduledMethodNode.getData())) {
//                tree.expandPath(new TreePath(childNode.getPath()));
//            }
//        }
//    }
//
//    /**
//     * 是否可以展开节点
//     *
//     * @param keyword 关键字
//     * @param data    数据
//     * @return 是否展开
//     */
//    private <T extends BasicComponent> boolean expandControllerData(String keyword, T data) {
//        if (data instanceof DynamicController) {
//            DynamicController controller = (DynamicController) data;
//            String className = controller.getSimpleClassName();
//            String url = controller.getUrl();
//            return className.toUpperCase().contains(keyword.toUpperCase()) || url.toUpperCase().contains(keyword.toUpperCase());
//        } else if (data instanceof StaticController) {
//            StaticController controller = (StaticController) data;
//            String className = controller.getSimpleClassName();
//            String url = controller.getUrl();
//            return className.toUpperCase().contains(keyword.toUpperCase()) || url.toUpperCase().contains(keyword.toUpperCase());
//        } else if (data instanceof SpringScheduled) {
//            SpringScheduled scheduled = (SpringScheduled) data;
//            return scheduled.getClassName().toUpperCase().contains(keyword.toUpperCase()) ||
//                    scheduled.getMethodName().toUpperCase().contains(keyword.toUpperCase());
//        }
//        return false;
//    }
}
