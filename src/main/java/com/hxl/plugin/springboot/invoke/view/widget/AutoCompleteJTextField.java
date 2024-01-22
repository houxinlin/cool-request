package com.hxl.plugin.springboot.invoke.view.widget;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTextField;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 自定建议文本输入框
 */
public class AutoCompleteJTextField extends JBTextField {

    private SuggestJWindow suggestJWindow;
    private static List<SuggestJWindow.Item> functionItem = new ArrayList<>();

    static {
        functionItem.add(new SuggestJWindow.FunctionItem("currentDate()", () -> LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
        functionItem.add(new SuggestJWindow.FunctionItem("timestamp()", () -> String.valueOf(System.currentTimeMillis())));
    }

    public AutoCompleteJTextField(List<String> suggest, Project project) {
        suggestJWindow = SuggestJWindow.attachJTextField(this, mergerFunction(suggest), project);
    }

    public void setSuggest(List<String> suggest) {
        suggestJWindow.setSuggest(mergerFunction(suggest));
    }

    private List<SuggestJWindow.Item> mergerFunction(List<String> suggest) {
        List<SuggestJWindow.Item> result = new ArrayList<>();
        result.addAll(suggest.stream().map((Function<String, SuggestJWindow.Item>)
                        s -> new SuggestJWindow.SimpleStringItem(s))
                .collect(Collectors.toList()));
        result.addAll(functionItem);
        return result;
    }
}