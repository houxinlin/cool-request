package com.cool.request.action.actions;

import com.cool.request.common.icons.CoolRequestIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.keymap.impl.ui.KeymapPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.fields.ExpandableTextField;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AboutAnAction extends BaseAnAction {
    public AboutAnAction(Project project) {
        super(project, () -> "About", () -> "About", CoolRequestIcons.MAIN);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<String> suggestions = new ArrayList<>();


        suggestions.add("Option 1");
        suggestions.add("Option 2");
        suggestions.add("Option 3");
        JBList<String> suggestionList = new JBList<>(suggestions.toArray(new String[0]));
        JBPopup popup = JBPopupFactory.getInstance()
                .createListPopupBuilder(suggestionList)
                .setTitle("Suggestions")
                .setItemChoosenCallback(() -> {
                    String selected = suggestionList.getSelectedValue();
                    if (selected != null) {
                    }
                })
                .createPopup();
        popup.showUnderneathOf(e.getInputEvent().getComponent());
//        new AboutDialog(getProject()).show();
    }

}
