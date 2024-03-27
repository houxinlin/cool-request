/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CopyHttpUrlAnAction.java is part of Cool Request
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

package com.cool.request.action.copy;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.utils.ClipboardUtils;
import com.cool.request.utils.ControllerUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class CopyHttpUrlAnAction extends AnAction {
    private final MainTopTreeView mainTopTreeVi;

    public CopyHttpUrlAnAction(MainTopTreeView mainTopTreeView) {
        super("Http Url");
        getTemplatePresentation().setIcon(CoolRequestIcons.IC_HTTP);
        this.mainTopTreeVi = mainTopTreeView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(this.mainTopTreeVi.getTree());
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof MainTopTreeView.RequestMappingNode) {
            MainTopTreeView.RequestMappingNode requestMappingNode = (MainTopTreeView.RequestMappingNode) selectedPathIfOne.getLastPathComponent();
            ClipboardUtils.copyToClipboard(ControllerUtils.buildLocalhostUrl(requestMappingNode.getData()));
        }
    }
}
