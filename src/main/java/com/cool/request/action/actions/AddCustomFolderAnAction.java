package com.cool.request.action.actions;

import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.state.CustomControllerFolderPersistent;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.TreePathUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class AddCustomFolderAnAction extends BaseAnAction {
    private Tree tree;

    public AddCustomFolderAnAction(Project project, Tree tree) {
        super(project, () -> "Add Custom Folder", CoolRequestIcons.CUSTOM_FOLDER);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPath = TreeUtil.getSelectedPathIfOne(tree);
        String result = Messages.showInputDialog(ResourceBundleUtils.getString("input.folder.name"), ResourceBundleUtils.getString("tip"), AllIcons.Actions.Edit);
        if (!StringUtils.hasText(result)) return;
        if (TreePathUtils.is(selectedPath, MainTopTreeView.CustomControllerFolderNode.class)) {
            MainTopTreeView.CustomControllerFolderNode folderNode = TreePathUtils.getNode(selectedPath, MainTopTreeView.CustomControllerFolderNode.class);
            folderNode.getData().addItem(new CustomControllerFolderPersistent.Folder(result));
        } else {
            if (StringUtils.hasText(result)) {
                CustomControllerFolderPersistent.getInstance().getFolder().addItem(new CustomControllerFolderPersistent.Folder(result));
            }
        }
        ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.REFRESH_CUSTOM_FOLDER).event();

    }
}
