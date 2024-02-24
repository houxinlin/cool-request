package com.cool.request.action.actions;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.CustomController;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.state.CustomControllerFolderPersistent;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.function.Predicate;

public class DeleteCustomControllerAnAction extends BaseAnAction {
    private MainTopTreeView mainTopTreeView;

    public DeleteCustomControllerAnAction(Project project, MainTopTreeView mainTopTreeView) {
        super(project, () -> "Delete", CoolRequestIcons.DELETE);
        this.mainTopTreeView = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<TreePath> treePaths = TreeUtil.collectSelectedPaths(this.mainTopTreeView.getTree());
        for (TreePath treePath : treePaths) {
            Object lastPathComponent = treePath.getLastPathComponent();
            //如果是Controller
            if (lastPathComponent instanceof MainTopTreeView.CustomMappingNode) {
                Controller controller = ((MainTopTreeView.CustomMappingNode) lastPathComponent).getData();
                TreeNode parent = ((MainTopTreeView.CustomMappingNode) lastPathComponent).getParent();
                if (parent instanceof MainTopTreeView.CustomControllerFolderNode) {
                    //从文件夹删除
                    CustomControllerFolderPersistent.Folder folder = (CustomControllerFolderPersistent.Folder)
                            ((MainTopTreeView.CustomControllerFolderNode) parent).getUserObject();

                    folder.getControllers().removeIf(customController ->
                            StringUtils.isEqualsIgnoreCase(controller.getId(), customController.getId()));
                } else {
                    //从root下删除
                    CustomControllerFolderPersistent.Folder rootFolder = CustomControllerFolderPersistent.getInstance().getFolder();
                    rootFolder.getControllers().removeIf(value -> StringUtils.isEqualsIgnoreCase(value.getId(), controller.getId()));
                }
            }
            //如果删除的是目录
            if (lastPathComponent instanceof MainTopTreeView.CustomControllerFolderNode) {
                MainTopTreeView.CustomControllerFolderNode customControllerFolderNode = (MainTopTreeView.CustomControllerFolderNode) lastPathComponent;
                TreeNode parent = customControllerFolderNode.getParent();
                if (parent instanceof MainTopTreeView.CustomControllerFolderNode) {
                    //从文件夹下删除
                    CustomControllerFolderPersistent.Folder parentFolder = ((MainTopTreeView.CustomControllerFolderNode) parent).getData();
                    parentFolder.remove(customControllerFolderNode.getData());
                } else {
                    //从root目录下删除
                    CustomControllerFolderPersistent.Folder rootFolder = CustomControllerFolderPersistent.getInstance().getFolder();
                    rootFolder.remove(customControllerFolderNode.getData());
                }
            }
        }
        //刷新数据
        ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.REFRESH_CUSTOM_FOLDER).event();

    }
}
