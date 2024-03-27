/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BinaryRequestBodyPage.java is part of Cool Request
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

import com.cool.request.utils.StringUtils;
import com.cool.request.utils.file.FileChooseUtils;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BinaryRequestBodyPage extends JPanel {
    public static final String DEFAULT_NAME = "Click Select File";
    private Project project;
    private String selectFile;

    private final JLabel jLabel = new JLabel(DEFAULT_NAME);

    public BinaryRequestBodyPage(Project project) {
        super(new BorderLayout());
        this.project = project;
        this.add(jLabel, BorderLayout.CENTER);
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setVerticalAlignment(SwingConstants.CENTER);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String file = FileChooseUtils.chooseSingleFile(project, null, null);
                if (!StringUtils.isEmpty(file) && Files.exists(Paths.get(file))) {
                    selectFile = file;
                    jLabel.setText(selectFile);
                }
            }
        });
    }

    public String getSelectFile() {
        return selectFile;
    }

    public void setSelectFile(String selectFile) {
        if (StringUtils.isEmpty(selectFile)) selectFile = DEFAULT_NAME;
        this.selectFile = selectFile;
        this.jLabel.setText(selectFile);
    }

}
