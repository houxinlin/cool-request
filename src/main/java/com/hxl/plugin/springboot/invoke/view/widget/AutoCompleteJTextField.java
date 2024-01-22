package com.hxl.plugin.springboot.invoke.view.widget;

import com.hxl.plugin.springboot.invoke.utils.file.FileChooseUtils;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTextField;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private List<SuggestJWindow.Item> functionItem = new ArrayList<>();

    {
        functionItem.add(new SuggestJWindow.FunctionItem("currentDateTime()", () -> LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
        functionItem.add(new SuggestJWindow.FunctionItem("currentDateTime2()", () -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.now().format(formatter);
        }));
        functionItem.add(new SuggestJWindow.FunctionItem("timestamp()", () -> String.valueOf(System.currentTimeMillis())));
        functionItem.add(new SuggestJWindow.FunctionItem("year()", () -> String.valueOf(LocalDate.now().getYear())));
        functionItem.add(new SuggestJWindow.FunctionItem("month()", () -> String.valueOf(LocalDate.now().getMonthValue())));
        functionItem.add(new SuggestJWindow.FunctionItem("day()", () -> String.valueOf(LocalDate.now().getDayOfMonth())));
        functionItem.add(new SuggestJWindow.FunctionItem("time()", () -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return LocalTime.now().format(formatter);
        }));
        functionItem.add(new SuggestJWindow.FunctionItem("date()", () -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.now().format(formatter);
        }));
    }

    public AutoCompleteJTextField(List<String> suggest, Project project) {
        suggestJWindow = SuggestJWindow.attachJTextField(this, mergerFunction(suggest), project);
        functionItem.add(new SuggestJWindow.FunctionItem("fileContent()", () -> {
            String file = FileChooseUtils.chooseSingleFile(project, null, null);
            if (file == null) return "";
            Path path = Paths.get(file);
            if (Files.exists(path)) {
                try {
                    return Files.readString(path);
                } catch (IOException e) {

                }
            }
            return "";
        }));

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