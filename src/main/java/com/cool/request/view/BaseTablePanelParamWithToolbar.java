package com.cool.request.view;

import com.cool.request.action.actions.DynamicAnAction;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * 定义一个基本的TABLE面板，具有增加、删除、复制行
 */
public abstract class BaseTablePanelParamWithToolbar extends JPanel {
    private final DefaultActionGroup menuGroup = new DefaultActionGroup();
    private Project project;
    private final ToolbarBuilder toolbarBuilder;
    protected JBTable jTable = new JBTable();

    public void addRow() {
    }

    public void removeRow() {
    }

    public void copyRow() {
    }

    public void saveRows() {
    }

    public void help() {
    }

    public BaseTablePanelParamWithToolbar(Project project, ToolbarBuilder builder) {
        super(new BorderLayout());
        this.project = project;
        this.toolbarBuilder = builder;
    }

    protected void showToolBar() {
        if (toolbarBuilder.addButton) menuGroup.add(new AddRowAnAction());
        if (toolbarBuilder.removeButton) menuGroup.add(new RemoveRowAnAction());
        if (toolbarBuilder.copyRowButton) menuGroup.add(new CopyRowAnAction());
        if (toolbarBuilder.saveButton) menuGroup.add(new SaveAnAction());
        if (toolbarBuilder.helpButton) menuGroup.add(new HelpAnAction());

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", menuGroup, true);
        toolbar.setTargetComponent(this);
        add(toolbar.getComponent(), BorderLayout.NORTH);

        add(new JBScrollPane(jTable), BorderLayout.CENTER);
        invalidate();
    }

    class HelpAnAction extends DynamicAnAction {
        public HelpAnAction() {
            super(null, () -> "Help", KotlinCoolRequestIcons.INSTANCE.getHELP());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            help();
        }
    }


    class SaveAnAction extends DynamicAnAction {
        public SaveAnAction() {
            super(null, () -> "Save", KotlinCoolRequestIcons.INSTANCE.getSAVE());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            saveRows();
        }
    }

    class AddRowAnAction extends DynamicAnAction {
        public AddRowAnAction() {
            super(null, () -> "Add New Row", KotlinCoolRequestIcons.INSTANCE.getADD());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            addRow();
        }
    }

    class RemoveRowAnAction extends DynamicAnAction {

        public RemoveRowAnAction() {
            super(null, () -> "Remove New Row", KotlinCoolRequestIcons.INSTANCE.getSUBTRACTION());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            removeRow();
        }
    }

    class CopyRowAnAction extends DynamicAnAction {
        public CopyRowAnAction() {
            super(null, () -> "Copy Row", KotlinCoolRequestIcons.INSTANCE.getCOPY());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            copyRow();
        }
    }

    public static class ToolbarBuilder {
        private boolean addButton;
        private boolean removeButton;
        private boolean copyRowButton;
        private boolean saveButton;
        private boolean helpButton;

        public ToolbarBuilder enabledAdd() {
            addButton = true;
            return this;
        }

        public ToolbarBuilder enabledHelp() {
            helpButton = true;
            return this;
        }

        public ToolbarBuilder enabledRemove() {
            removeButton = true;
            return this;
        }

        public ToolbarBuilder enabledCopyRow() {
            copyRowButton = true;
            return this;
        }

        public ToolbarBuilder enabledSaveButton() {
            saveButton = true;
            return this;
        }

        public ToolbarBuilder all() {
            addButton = true;
            copyRowButton = true;
            saveButton = true;
            removeButton = true;
            helpButton = true;
            return this;
        }
    }
}
