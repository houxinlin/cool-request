/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * AutoCompleteJTextField.java is part of Cool Request
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

package com.cool.request.view.widget;

import com.cool.request.utils.file.FileChooseUtils;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBTextField;

import java.awt.*;
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
import java.util.stream.Collectors;

/**
 * 自定建议文本输入框
 */
public class AutoCompleteJTextField extends JBTextField {

    private AutoSuggestor suggestJWindow;
    private List<AutoSuggestor.Item> functionItem = new ArrayList<>();

    {
        functionItem.add(new AutoSuggestor.FunctionItem("currentDateTime()", () -> LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
        functionItem.add(new AutoSuggestor.FunctionItem("currentDateTime2()", () -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.now().format(formatter);
        }));
        functionItem.add(new AutoSuggestor.FunctionItem("timestamp()", () -> String.valueOf(System.currentTimeMillis())));
        functionItem.add(new AutoSuggestor.FunctionItem("year()", () -> String.valueOf(LocalDate.now().getYear())));
        functionItem.add(new AutoSuggestor.FunctionItem("month()", () -> String.valueOf(LocalDate.now().getMonthValue())));
        functionItem.add(new AutoSuggestor.FunctionItem("day()", () -> String.valueOf(LocalDate.now().getDayOfMonth())));
        functionItem.add(new AutoSuggestor.FunctionItem("time()", () -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return LocalTime.now().format(formatter);
        }));
        functionItem.add(new AutoSuggestor.FunctionItem("date()", () -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.now().format(formatter);
        }));
    }

    public AutoCompleteJTextField(List<String> suggest, Project project, Window window) {
        //暂时指设置主窗口中的，dialog中有问题


//        if (window == null) {
//            suggestJWindow = AutoSuggestor.attachJTextField(this, window, mergerFunction(suggest), project);
//        }
//        functionItem.add(new AutoSuggestor.FunctionItem("fileContent()", () -> {
//            String file = FileChooseUtils.chooseSingleFile(project, null, null);
//            if (file == null) return "";
//            Path path = Paths.get(file);
//            if (Files.exists(path)) {
//                try {
//                    return Files.readString(path);
//                } catch (IOException e) {
//
//                }
//            }
//            return "";
//        }));

    }

    public void setSuggest(List<String> suggest) {
        if (suggestJWindow != null)
            suggestJWindow.setSuggest(mergerFunction(suggest));
    }

    private List<AutoSuggestor.Item> mergerFunction(List<String> suggest) {
        List<AutoSuggestor.Item> result = new ArrayList<>();
        result.addAll(suggest.stream().map((Function<String, AutoSuggestor.Item>)
                        s -> new AutoSuggestor.SimpleStringItem(s))
                .collect(Collectors.toList()));
        result.addAll(functionItem);
        return result;
    }
}