package com.hxl.plugin.springboot.invoke.plugin.apifox;

import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import java.util.Map;

public class CreateNewFolderAction extends AnAction {
    private final ApifoxAPI apifoxAPI;
    private final SimpleTree simpleTree;
    private final RefreshCallback callback;

    @FunctionalInterface
    public interface RefreshCallback {
        void addNewFolder(ApifoxProjectFolderSelectDialog.FolderTreeNode folderTreeNode, ApifoxFolder.Folder folder);
    }

    public CreateNewFolderAction(ApifoxAPI apifoxAPI, SimpleTree simpleTree, RefreshCallback callback) {
        super("New Folder");
        this.apifoxAPI = apifoxAPI;
        this.simpleTree = simpleTree;
        this.callback = callback;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String result = Messages.showInputDialog("输入名称", "提示", AllIcons.Actions.Edit);
        if (!StringUtils.isEmpty(result)) {
            TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.simpleTree);
            if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof ApifoxProjectFolderSelectDialog.FolderTreeNode) {
                int id = ((ApifoxProjectFolderSelectDialog.FolderTreeNode) selectedPathIfOne.getLastPathComponent()).getFolder().getId();
                new Thread(() -> {
                    int projectId = ((ApifoxProjectFolderSelectDialog.FolderTreeNode) selectedPathIfOne.getLastPathComponent()).getFolder().getProjectId();
                    Map<String, Object> createResult = apifoxAPI.createNewFolderAndGet(id, result, projectId);
                    if (createResult.getOrDefault("success", false).equals(Boolean.TRUE)) {
                        String data = ObjectMappingUtils.toJsonString(createResult.get("data"));
                        ApifoxFolder.Folder folder = ObjectMappingUtils.readValue(data, ApifoxFolder.Folder.class);
                        callback.addNewFolder(((ApifoxProjectFolderSelectDialog.FolderTreeNode) selectedPathIfOne.getLastPathComponent()), folder);
                    } else {
                        Messages.showErrorDialog("创建失败:" + createResult.getOrDefault("errorMessage", ""), "提示");
                    }
                }).start();
            } else {
                Messages.showErrorDialog("创建失败，无法在此节点创建目录", "提示");
            }
        }
    }
}
