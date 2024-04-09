/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SearchKeyAction.java is part of Cool Request
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

import com.cool.request.common.state.CommonStatePersistent;
import com.cool.request.view.tool.search.ApiAbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class SearchKeyAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String searchCache = CommonStatePersistent.getInstance(e.getProject()).searchCache;
        SearchEverywhereManager seManager = SearchEverywhereManager.getInstance(e.getProject());
        seManager.show(ApiAbstractGotoSEContributor.class.getSimpleName(), searchCache, e);
    }
}
