/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BigInputDialog.java is part of Cool Request
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

package com.cool.request.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BigInputDialog extends DialogWrapper {
    private JPanel root;
    private JTextArea textArea;
    private JLabel tip;

    public BigInputDialog(@Nullable Project project, String tip) {
        super(project);
        this.tip.setText(tip);
        setSize(600,400);
        init();
    }

    public BigInputDialog(@Nullable Project project) {
        super(project);
        this.tip.setText("");
        setSize(600,400);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        textArea.setLineWrap(true);
        return root;
    }

    public String getValue() {
        return textArea.getText();
    }
}
