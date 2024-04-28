/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RestfulTreeCellRenderer.java is part of Cool Request
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

package com.cool.request.view;


import com.cool.request.common.bean.components.DynamicComponent;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.cool.request.components.http.Controller;
import com.cool.request.components.http.CustomController;
import com.cool.request.components.scheduled.BasicScheduled;
import com.cool.request.components.scheduled.DynamicSpringScheduled;
import com.cool.request.components.scheduled.DynamicXxlJobScheduled;
import com.cool.request.components.scheduled.XxlJobScheduled;
import com.cool.request.lib.springmvc.MethodDescription;
import com.cool.request.utils.ControllerUtils;
import com.cool.request.utils.HttpMethodIconUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.widget.MergeIcon;
import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Optional;


public class RestfulTreeCellRenderer extends ColoredTreeCellRenderer {
    private final SimpleTextAttributes SUMMARY = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, CoolRequestConfigConstant.Colors.Summary);
    private final SettingsState settingsState = SettingPersistentState.getInstance().getState();

    public RestfulTreeCellRenderer() {
    }

    @Override
    public void customizeCellRenderer(
            @NotNull JTree tree, Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row, boolean hasFocus) {
        if (value instanceof MainTopTreeView.XxlJobMethodNode) {
            MainTopTreeView.XxlJobMethodNode node = (MainTopTreeView.XxlJobMethodNode) value;
            setIcon(getIcon(node.getData()));
            append(node.getData().getMethodName());
        } else if (value instanceof MainTopTreeView.SpringScheduledMethodNode) {
            MainTopTreeView.SpringScheduledMethodNode node = (MainTopTreeView.SpringScheduledMethodNode) value;
            setIcon(getIcon(node.getData()));
            append(node.getData().getMethodName());
        } else if (value instanceof MainTopTreeView.CustomControllerFolderNode) {
            MainTopTreeView.CustomControllerFolderNode node = (MainTopTreeView.CustomControllerFolderNode) value;
            setIcon(CoolRequestIcons.CUSTOM_FOLDER);
            append(node.getData().getName());
        } else if (value instanceof MainTopTreeView.FeaturesModuleNode) {
            MainTopTreeView.FeaturesModuleNode node = (MainTopTreeView.FeaturesModuleNode) value;
            setIcon(AllIcons.Nodes.ModuleGroup);
            append(node.getData());
        } else if (value instanceof MainTopTreeView.PackageNameNode) {
            MainTopTreeView.TreeNode<?> node = (MainTopTreeView.TreeNode<?>) value;
            setIcon(AllIcons.Nodes.Package);
            append(node.toString());
        } else if (value instanceof MainTopTreeView.ClassNameNode) {
            MainTopTreeView.TreeNode<?> node = (MainTopTreeView.TreeNode<?>) value;
            setIcon(AllIcons.Nodes.Class);
            append(node.toString());
        } else if (value instanceof MainTopTreeView.ProjectModuleNode) {
            MainTopTreeView.TreeNode<?> node = (MainTopTreeView.TreeNode<?>) value;
            setIcon(AllIcons.Actions.ModuleDirectory);
            append(node.toString());
        } else if (value instanceof MainTopTreeView.RequestMappingNode) {
            MainTopTreeView.RequestMappingNode node = (MainTopTreeView.RequestMappingNode) value;
            Controller controller = node.getData();
            setIcon(getIcon(controller));
            if (!settingsState.onlySummary) {
                append(ControllerUtils.getFullUrl(node.getData()));
            }
            if (settingsState.showSummary) {
                append(" ");
                if (controller instanceof CustomController) {
                    append(Optional.ofNullable(((CustomController) controller).getSummary()).orElse(""), SUMMARY);
                } else {
                    MethodDescription methodDescription = controller.getMethodDescription();
                    if (methodDescription != null && methodDescription.getSummary() != null) {
                        append(methodDescription.getSummary(), SUMMARY);
                    }
                }
            }
        } else if (value instanceof MainTopTreeView.TreeNode<?>) {
            MainTopTreeView.TreeNode<?> node = (MainTopTreeView.TreeNode<?>) value;
            append(node.toString());
        }
    }

    private Icon getIcon(Controller controller) {
//        if (controller instanceof DynamicComponent) {
//            return new MergeIcon(CoolRequestIcons.LIGHTNING, HttpMethodIconUtils.getIconByHttpMethod(controller.getHttpMethod()));
//        }
        return HttpMethodIconUtils.getIconByHttpMethod(controller.getHttpMethod());

    }

    private Icon getIcon(BasicScheduled springScheduled) {
        if (springScheduled instanceof DynamicXxlJobScheduled) {
            return new MergeIcon(CoolRequestIcons.LIGHTNING, CoolRequestIcons.XXL_JOB, CoolRequestIcons.TIMER);
        }
        if (springScheduled instanceof DynamicSpringScheduled) {
            return new MergeIcon(CoolRequestIcons.LIGHTNING, CoolRequestIcons.TIMER);
        }
        if (springScheduled instanceof XxlJobScheduled) {
            return new MergeIcon(CoolRequestIcons.TIMER, CoolRequestIcons.XXL_JOB);
        }
        return CoolRequestIcons.TIMER;
    }

}