/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * DeleteCustomControllerAnAction.java is part of Cool Request
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

import com.cool.request.components.http.Controller;
import com.cool.request.common.cache.CacheStorageService;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
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
import java.util.ArrayList;
import java.util.List;

public class DeleteCustomControllerAnAction extends DynamicAnAction {
    private MainTopTreeView mainTopTreeView;

    public DeleteCustomControllerAnAction(Project project, MainTopTreeView mainTopTreeView) {
        super(project, () -> "Delete", KotlinCoolRequestIcons.INSTANCE.getDELETE());
        this.mainTopTreeView = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<TreePath> treePaths = TreeUtil.collectSelectedPaths(this.mainTopTreeView.getTree());
        List<Controller> removeCache = new ArrayList<>();
        for (TreePath treePath : treePaths) {
            Object lastPathComponent = treePath.getLastPathComponent();
            //如果是Controller
            if (lastPathComponent instanceof MainTopTreeView.CustomMappingNode) {
                Controller controller = ((MainTopTreeView.CustomMappingNode) lastPathComponent).getData();
                removeCache.add(controller);
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
                removeCache.addAll(customControllerFolderNode.getData().getControllers());
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

        //将请求参数缓存删除
        CacheStorageService service = ApplicationManager.getApplication().getService(CacheStorageService.class);
        for (Controller controller : removeCache) {
            if (StringUtils.isEmpty(controller)) continue;
            ComponentCacheManager.removeCache(controller.getId());
            service.removeResponseCache(controller.getId());
        }
    }
}
