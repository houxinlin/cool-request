package com.cool.request.action.actions;

import com.cool.request.view.tool.search.ApiAbstractGotoSEContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class SearchKeyAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        SearchEverywhereManager seManager = SearchEverywhereManager.getInstance(e.getProject());
        seManager.show(ApiAbstractGotoSEContributor.class.getSimpleName(), "", e);
    }
}
