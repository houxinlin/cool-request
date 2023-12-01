package com.hxl.plugin.springboot.invoke.action;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.utils.NotifyUtils;
import com.hxl.plugin.springboot.invoke.utils.RequestParamCacheManager;
import com.hxl.plugin.springboot.invoke.utils.service.CacheStorageService;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import java.util.List;

public class CleanCacheAnAction  extends AnAction {
    private final SimpleTree simpleTree;
    private  final MainTopTreeView mainTopTreeView;
    public CleanCacheAnAction(MainTopTreeView mainTopTreeView) {
        super("Clear Request Cache");
        getTemplatePresentation().setIcon(MyIcons.DELETE);
        this.simpleTree = ((SimpleTree) mainTopTreeView.getTree());
        this.mainTopTreeView = mainTopTreeView;
    }
    private void clearRequestCache(RequestMappingModel requestMappingNode){
        RequestParamCacheManager.removeCache(requestMappingNode.getController().getId());
        ApplicationManager.getApplication().getService(CacheStorageService.class).deleteResponseCache(requestMappingNode.getController().getId());
        mainTopTreeView.getProject().getMessageBus().syncPublisher(IdeaTopic.CLEAR_REQUEST_CACHE).onClearEvent(requestMappingNode.getController().getId());
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.simpleTree);
        if (selectedPathIfOne!=null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.ModuleNode){
            String data = ((MainTopTreeView.ModuleNode) selectedPathIfOne.getLastPathComponent()).getData();
            if ("Controller".equalsIgnoreCase(data)){
                for (List<MainTopTreeView.RequestMappingNode> value : mainTopTreeView.getRequestMappingNodeMap().values()) {
                    for (MainTopTreeView.RequestMappingNode requestMappingNode : value) {
                        clearRequestCache(requestMappingNode.getData());
                    }
                }
            }
        }
        if (selectedPathIfOne!=null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.ClassNameNode){
            MainTopTreeView.ClassNameNode classNameNode = (MainTopTreeView.ClassNameNode) selectedPathIfOne.getLastPathComponent();
            for (MainTopTreeView.RequestMappingNode requestMappingNode : mainTopTreeView.getRequestMappingNodeMap().
                    getOrDefault(classNameNode, List.of())) {
                clearRequestCache(requestMappingNode.getData());
            }
        }
        if (selectedPathIfOne!=null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RequestMappingNode){
            MainTopTreeView.RequestMappingNode requestMappingNode = (MainTopTreeView.RequestMappingNode) selectedPathIfOne.getLastPathComponent();
            clearRequestCache(requestMappingNode.getData());

        }
        NotifyUtils.notification("Clear Success");
    }
}
