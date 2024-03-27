/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * AddCustomFolderAnAction.java is part of Cool Request
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
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class AddCustomFolderAnAction extends BaseAnAction {
    private final MainTopTreeView mainTopTreeView;

    public AddCustomFolderAnAction(Project project, MainTopTreeView mainTopTreeView) {
        super(project, () -> ResourceBundleUtils.getString("add.custom.folder"), CoolRequestIcons.CUSTOM_FOLDER);
        this.mainTopTreeView = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPath = TreeUtil.getSelectedPathIfOne(mainTopTreeView.getTree());
        String result = Messages.showInputDialog(ResourceBundleUtils.getString("input.folder.name"), ResourceBundleUtils.getString("tip"), AllIcons.Actions.Edit);
        if (!StringUtils.hasText(result)) return;
        MainTopTreeView.CustomControllerFolderNode folderNode = null;
        if (TreePathUtils.is(selectedPath, MainTopTreeView.CustomControllerFolderNode.class)) {
            //在自定义目录中添加
            folderNode = TreePathUtils.getNode(selectedPath, MainTopTreeView.CustomControllerFolderNode.class);
            if (folderNode == null) return;
            CustomControllerFolderPersistent.Folder folder = new CustomControllerFolderPersistent.Folder(result);
            folderNode.getData().addSubFolder(folder);

        } else {
            //在root目录下添加
            CustomControllerFolderPersistent.Folder folder = new CustomControllerFolderPersistent.Folder(result);
            CustomControllerFolderPersistent.getInstance().getFolder().addSubFolder(folder);
        }
        //通知刷新数据
        ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.REFRESH_CUSTOM_FOLDER).event();
        //将新添加的节点展开
        if (folderNode != null) {
            TreePath pathToExpand = new TreePath(folderNode.getPath());
            mainTopTreeView.getTree().expandPath(pathToExpand);
        }

    }
}
