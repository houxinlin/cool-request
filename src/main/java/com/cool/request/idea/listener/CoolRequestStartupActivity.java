package com.cool.request.idea.listener;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.cool.request.component.http.net.VersionInfoReport;
import com.intellij.ide.util.RunOnceUtil;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.KeymapManager;
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
    }
}
