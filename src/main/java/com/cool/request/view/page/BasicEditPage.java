/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BasicEditPage.java is part of Cool Request
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

package com.cool.request.view.page;

import com.cool.request.components.CoolRequestPluginDisposable;
import com.cool.request.utils.ReflexUtils;
import com.cool.request.view.MultilingualEditor;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

public abstract class BasicEditPage extends JPanel implements Disposable {
    private final MultilingualEditor editor;
    public abstract FileType getFileType();
    public BasicEditPage(Project project) {
        editor = new MultilingualEditor(project, getFileType());
        setLayout(new BorderLayout());
        add(editor, BorderLayout.CENTER);
        ReflexUtils.invokeMethod(editor,
                "setDisposedWith",
                new Object[]{CoolRequestPluginDisposable.getInstance()},
                Disposable.class);
    }

    @Override
    public void dispose() {
        editor.dispose();
    }

    public void setText(String text) {
        SwingUtilities.invokeLater(() -> editor.setText(text));
    }

    public String getText() {
        return editor.getText();
    }
}
