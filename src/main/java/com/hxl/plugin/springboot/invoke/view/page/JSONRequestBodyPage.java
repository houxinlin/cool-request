package com.hxl.plugin.springboot.invoke.view.page;

import com.hxl.plugin.springboot.invoke.action.response.BaseAction;
import com.hxl.plugin.springboot.invoke.utils.ObjectMappingUtils;
import com.hxl.plugin.springboot.invoke.utils.file.FileChooseUtils;
import com.hxl.plugin.springboot.invoke.view.MultilingualEditor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JSONRequestBodyPage extends SimpleToolWindowPanel {
    private final BasicJSONRequestBodyPage innerJSONRequestBodyPage;

    public JSONRequestBodyPage(Project project) {
        super(false);
        innerJSONRequestBodyPage = new BasicJSONRequestBodyPage(project);
        DefaultActionGroup toolGroup = new DefaultActionGroup();
        toolGroup.add(new BaseAction("Beautify", AllIcons.General.InspectionsEye) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                setText(ObjectMappingUtils.format(getText()));
            }
        });

        ActionToolbar rightToolBar = ActionManager.getInstance().createActionToolbar("right-bar", toolGroup, false);
        rightToolBar.setTargetComponent(this);
        JPanel rightTool = new JPanel(new BorderLayout());
        rightTool.add(rightToolBar.getComponent());
        JPanel root = new JPanel(new BorderLayout());
        root.add(innerJSONRequestBodyPage, BorderLayout.CENTER);
        root.add(rightTool, BorderLayout.EAST);
        add(root);
    }

    public String getText() {
       return innerJSONRequestBodyPage.getText();
    }

    public void setText(String textBody) {
        innerJSONRequestBodyPage.setText(textBody);
    }

    static class BasicJSONRequestBodyPage extends BasicEditPage {
        public BasicJSONRequestBodyPage(Project project) {
            super(project);
        }

        @Override
        public FileType getFileType() {
            return MultilingualEditor.JSON_FILE_TYPE;
        }
    }
}
