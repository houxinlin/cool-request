package com.hxl.plugin.springboot.invoke.action;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.utils.NotifyUtils;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.plugin.springboot.invoke.utils.ResourceBundleUtils;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import java.util.List;

/**
 * This class represents an action to clean cache in the application.
 * It extends the AnAction class provided by IntelliJ IDEA's action system.
 */
public class CleanCacheAnAction extends AnAction {
    private final SimpleTree simpleTree;
    private final MainTopTreeView mainTopTreeView;

    /**
     * Constructor for the CleanCacheAnAction class.
     *
     * @param mainTopTreeView The main view of the application.
     */
    public CleanCacheAnAction(MainTopTreeView mainTopTreeView) {
        super(ResourceBundleUtils.getString("clear.request.cache"));
        getTemplatePresentation().setIcon(MyIcons.DELETE);
        this.simpleTree = ((SimpleTree) mainTopTreeView.getTree());
        this.mainTopTreeView = mainTopTreeView;
    }

    /**
     * This method is called when the action is performed.
     * It clears the cache based on the selected node in the tree view.
     *
     * @param e The event object associated with the action.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.simpleTree);
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.FeaturesModuleNode) {
            String data = ((MainTopTreeView.FeaturesModuleNode) selectedPathIfOne.getLastPathComponent()).getData();
            if ("Controller".equalsIgnoreCase(data)) {
                for (List<MainTopTreeView.RequestMappingNode> value : mainTopTreeView.getRequestMappingNodeMap().values()) {
                    for (MainTopTreeView.RequestMappingNode requestMappingNode : value) {
                        RequestParamCacheManager.removeCache(requestMappingNode.getData().getId());
                    }
                }
            }
        }
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.ClassNameNode) {
            MainTopTreeView.ClassNameNode classNameNode = (MainTopTreeView.ClassNameNode) selectedPathIfOne.getLastPathComponent();
            for (MainTopTreeView.RequestMappingNode requestMappingNode : mainTopTreeView.getRequestMappingNodeMap().
                    getOrDefault(classNameNode, List.of())) {
                RequestParamCacheManager.removeCache(requestMappingNode.getData().getId());

            }
        }
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RequestMappingNode) {
            MainTopTreeView.RequestMappingNode requestMappingNode = (MainTopTreeView.RequestMappingNode) selectedPathIfOne.getLastPathComponent();
            RequestParamCacheManager.removeCache(requestMappingNode.getData().getId());

        }
        if (mainTopTreeView.getCurrentTreeNode() instanceof MainTopTreeView.RequestMappingNode) {
            Object data = mainTopTreeView.getCurrentTreeNode().getData();
            mainTopTreeView.getProject().getMessageBus().syncPublisher(IdeaTopic.CONTROLLER_CHOOSE_EVENT).refreshEvent(((Controller) data));
        }
        NotifyUtils.notification(mainTopTreeView.getProject(), "Clear Success");
    }
}
