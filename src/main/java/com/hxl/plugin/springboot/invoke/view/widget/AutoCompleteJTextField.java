package com.hxl.plugin.springboot.invoke.view.widget;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTextField;

import java.util.List;

/**
 * 自定建议文本输入框
 */
public class AutoCompleteJTextField extends JBTextField {

    private SuggestJWindow suggestJWindow;

    public AutoCompleteJTextField(List<String> suggest, Project project) {
        suggestJWindow = SuggestJWindow.attachJTextField(this, suggest, project);
    }

    public void setSuggest(List<String> suggest) {
        suggestJWindow.setSuggest(suggest);
    }
}