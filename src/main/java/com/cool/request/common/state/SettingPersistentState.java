/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SettingPersistentState.java is part of Cool Request
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

package com.cool.request.common.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@State(name = "CoolRequestSetting", storages = @Storage("CoolRequestSetting.xml"))
@Service()
public final class SettingPersistentState implements PersistentStateComponent<SettingsState> {
    public static SettingPersistentState getInstance() {
        return ApplicationManager.getApplication().getService(SettingPersistentState.class);
    }
    private KeyStroke currentKeyStroke;
    public SettingPersistentState() {
        state= new SettingsState();
    }

    public KeyStroke getCurrentKeyStroke() {
        return currentKeyStroke;
    }

    public void setCurrentKeyStroke(KeyStroke currentKeyStroke) {
        this.currentKeyStroke = currentKeyStroke;
    }

    private SettingsState state ;
    @Override
    public @NotNull SettingsState getState() {
        return state;
    }
    @Override
    public void loadState(@NotNull SettingsState state) {
        this.state=state;

    }
}