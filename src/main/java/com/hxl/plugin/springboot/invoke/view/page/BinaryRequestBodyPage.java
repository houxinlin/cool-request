package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.utils.file.FileChooseUtils;
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
