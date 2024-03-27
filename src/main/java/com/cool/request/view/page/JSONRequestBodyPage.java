/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * JSONRequestBodyPage.java is part of Cool Request
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

package com.cool.request.view.page;

import com.cool.request.action.response.BaseAction;
import com.cool.request.utils.GsonUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class JSONRequestBodyPage extends SimpleToolWindowPanel implements Disposable {
    private final BasicJSONRequestBodyPage innerJSONRequestBodyPage;

    public JSONRequestBodyPage(Project project) {
        super(false);
        innerJSONRequestBodyPage = new BasicJSONRequestBodyPage(project);
        DefaultActionGroup toolGroup = new DefaultActionGroup();
        toolGroup.add(new BaseAction("Beautify", AllIcons.General.InspectionsEye) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                setText(GsonUtils.format(getText()));
            }
        });

        ActionToolbar rightToolBar = ActionManager.getInstance().createActionToolbar("right-bar", toolGroup, false);
        rightToolBar.setTargetComponent(this);
        JPanel rightTool = new JPanel(new BorderLayout());
        rightTool.add(rightToolBar.getComponent());
        JPanel root = new JPanel(new BorderLayout());
        root.add(innerJSONRequestBodyPage, BorderLayout.CENTER);
        root.add(rightTool, BorderLayout.EAST);
        add(root);
    }

    @Override
    public void dispose() {
        innerJSONRequestBodyPage.dispose();
    }

    public String getText() {
        return innerJSONRequestBodyPage.getText();
    }

    public void setText(String textBody) {
        innerJSONRequestBodyPage.setText(textBody);
    }


}