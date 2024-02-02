package com.cool.request.view.page;

import com.cool.request.action.response.BaseAction;
import com.cool.request.utils.GsonUtils;
import com.cool.request.view.MultilingualEditor;
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

public class JSONRequestBodyPage extends SimpleToolWindowPanel {
    private final BasicJSONRequestBodyPage innerJSONRequestBodyPage;

    public JSONRequestBodyPage(Project project) {
        super(false);
        innerJSONRequestBodyPage = new BasicJSONRequestBodyPage(project);
        DefaultActionGroup toolGroup = new DefaultActionGroup();
        toolGroup.add(new BaseAction("Beautify", AllIcons.General.InspectionsEye) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                setText(GsonUtils.format(getText()));
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
