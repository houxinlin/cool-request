/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CoolRequestStartupActivity.java is part of Cool Request
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

package com.cool.request.idea.listener;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.cool.request.components.http.net.VersionInfoReport;
import com.cool.request.view.tool.CoolRequest;
import com.intellij.ide.util.RunOnceUtil;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CoolRequestStartupActivity implements StartupActivity {
    private static final VersionInfoReport versionReport = new VersionInfoReport();
    private static final String REGISTER_HOT_KEY = "REGISTER_HOT_KEY@CoolRequest";

    @Override
    public void runActivity(@NotNull Project project) {
        RunOnceUtil.runOnceForApp(REGISTER_HOT_KEY, () -> {
            versionReport.report();
            SettingsState state = SettingPersistentState.getInstance().getState();
            KeyStroke keyStroke = KeyStroke.getKeyStroke(state.searchApiKeyCode, state.searchApiModifiers, false);
            SettingPersistentState.getInstance().setCurrentKeyStroke(keyStroke);
            Shortcut shortcut = new KeyboardShortcut(keyStroke, null);
            KeymapManager.getInstance().getActiveKeymap().addShortcut("com.cool.request.HotkeyAction", shortcut);
        });
        //在线程中初始化的原因是，绑定RMI服务可能耗时
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Cool Request init") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                CoolRequest.getInstance(project).init();
            }
        });
    }
}
