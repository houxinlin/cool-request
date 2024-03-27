/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * FindAction.java is part of Cool Request
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

import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.tool.search.ApiAbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author caoayu
 */
public class FindAction extends DynamicAnAction {
    public FindAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("find"),
                () -> ResourceBundleUtils.getString("find"),
                KotlinCoolRequestIcons.INSTANCE.getSEARCH());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        int searchApiKeyCode = SettingPersistentState.getInstance().getState().searchApiKeyCode;
        int searchApiModifiers = SettingPersistentState.getInstance().getState().searchApiModifiers;
        KeyStroke keyStroke = KeyStroke.getKeyStroke(searchApiKeyCode, searchApiModifiers, false);
        e.getPresentation().setText(KeymapUtil.getKeystrokeText(keyStroke));
        getTemplatePresentation().setText(KeymapUtil.getKeystrokeText(keyStroke));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        SearchEverywhereManager seManager = SearchEverywhereManager.getInstance(e.getProject());
        seManager.show(ApiAbstractGotoSEContributor.class.getSimpleName(), "", e);

    }

}
