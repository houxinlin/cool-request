package com.cool.request.action.actions;

import com.cool.request.tool.search.ApiAbstractGotoSEContributor;
import com.cool.request.utils.ResourceBundleUtils;
import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

/**
 * @author caoayu
 */
public class FindAction extends BaseAnAction {
    public FindAction(Project project) {
        super(project, () -> ResourceBundleUtils.getString("find"), () -> ResourceBundleUtils.getString("find"),
                AllIcons.Actions.Find);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        SearchEverywhereManager seManager = SearchEverywhereManager.getInstance(e.getProject());
        seManager.show(ApiAbstractGotoSEContributor.class.getSimpleName(), "", e);

    }

}
