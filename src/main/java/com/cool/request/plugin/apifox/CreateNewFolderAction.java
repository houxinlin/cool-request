/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CreateNewFolderAction.java is part of Cool Request
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

package com.cool.request.plugin.apifox;

import com.cool.request.utils.GsonUtils;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
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
        String result = Messages.showInputDialog("Input name", ResourceBundleUtils.getString("tip"), AllIcons.Actions.Edit);
        if (!StringUtils.isEmpty(result)) {
            TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.simpleTree);
            if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof ApifoxProjectFolderSelectDialog.FolderTreeNode) {
                int id = ((ApifoxProjectFolderSelectDialog.FolderTreeNode) selectedPathIfOne.getLastPathComponent()).getFolder().getId();
                new Thread(() -> {
                    int projectId = ((ApifoxProjectFolderSelectDialog.FolderTreeNode) selectedPathIfOne.getLastPathComponent()).getFolder().getProjectId();
                    Map<String, Object> createResult = apifoxAPI.createNewFolderAndGet(id, result, projectId);
                    if (createResult.getOrDefault("success", false).equals(Boolean.TRUE)) {
                        String data = GsonUtils.toJsonString(createResult.get("data"));
                        ApifoxFolder.Folder folder = GsonUtils.readValue(data, ApifoxFolder.Folder.class);
                        callback.addNewFolder(((ApifoxProjectFolderSelectDialog.FolderTreeNode) selectedPathIfOne.getLastPathComponent()), folder);
                    } else {
                        MessagesWrapperUtils.showErrorDialog("Create Fail:" + createResult.getOrDefault("errorMessage", ""), ResourceBundleUtils.getString("tip"));
                    }
                }).start();
            } else {
                Messages.showErrorDialog("Creation failed, unable to create directory on this node", "提示");
            }
        }
    }
}
