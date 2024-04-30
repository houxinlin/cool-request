/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * cURLListener.java is part of Cool Request
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

package com.cool.request.common.listener.component;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.service.ClipboardService;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.components.CoolRequestContext;
import com.cool.request.utils.ClipboardUtils;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.main.IRequestParamManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.FocusWatcher;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.cool.request.common.constant.CoolRequestConfigConstant.PLUGIN_ID;

/**
 * curl监听器
 */
public class cURLListener extends FocusWatcher {
    private Project project;
    private String lastContent;
    private Component component;

    public cURLListener(Project project, Component component) {
        this.project = project;
        this.component = component;
    }

    @Override
    protected void focusedComponentChanged(@Nullable Component focusedComponent, @Nullable AWTEvent cause) {
        super.focusedComponentChanged(component, cause);
        if (focusedComponent != null && SwingUtilities.isDescendingFrom(focusedComponent, component)) {
            if (!SettingPersistentState.getInstance().getState().listenerCURL) return;
            String newContent = ClipboardUtils.getClipboardText();
            if (newContent != null && (!newContent.equals(lastContent))) {
                if (StringUtils.isEqualsIgnoreCase(ClipboardService.getInstance().getCurlData(), newContent)) return;
                if (StringUtils.isStartWithIgnoreSpace(newContent, "curl")) {
                    IRequestParamManager mainRequestParamManager = CoolRequestContext.getInstance(project).getMainRequestParamManager();
                    MessagesWrapperUtils.showOkCancelDialog(ResourceBundleUtils.getString("import.curl.tip.auto"),
                            ResourceBundleUtils.getString("tip"), CoolRequestIcons.MAIN, integer -> {
                                if (0 == integer) mainRequestParamManager.importCurl(newContent);
                            });
                }
            }
            lastContent = ClipboardUtils.getClipboardText();
        }
    }

}
