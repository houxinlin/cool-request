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
