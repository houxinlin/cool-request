package com.cool.request.view;

import com.cool.request.action.actions.BaseAnAction;
import com.cool.request.common.icons.CoolRequestIcons;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 定义一个基本的TABLE面板，具有增加、删除、复制行
 */
public abstract class BaseTablePanelParamWithToolbar extends SimpleToolWindowPanel {
    private final DefaultActionGroup menuGroup = new DefaultActionGroup();
    private Project project;
    private ToolbarBuilder toolbarBuilder;

    public void addRow() {
    }

    public void removeRow() {
    }

    public void copyRow() {
    }

    public void saveRows() {
    }

    public BaseTablePanelParamWithToolbar(Project project, ToolbarBuilder builder) {
        super(true);
        this.project = project;
        this.toolbarBuilder = builder;
    }

    protected void showToolBar() {
        if (toolbarBuilder.addButton) menuGroup.add(new AddRowAnAction());
        if (toolbarBuilder.removeButton) menuGroup.add(new RemoveRowAnAction());
        if (toolbarBuilder.copyRowButton) menuGroup.add(new CopyRowAnAction());
        if (toolbarBuilder.saveButton) menuGroup.add(new SaveAnAction());

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("bar", menuGroup, false);

        toolbar.setTargetComponent(this);
        ((ActionToolbar) toolbar.getComponent()).setOrientation(myVertical ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL);
        setToolbar(toolbar.getComponent());
    }


    class SaveAnAction extends BaseAnAction {
        public SaveAnAction() {
            super(null, () -> "Save", CoolRequestIcons.SAVE);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            saveRows();
        }
    }

    class AddRowAnAction extends BaseAnAction {
        public AddRowAnAction() {
            super(null, () -> "Add New Row", AllIcons.General.Add);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            addRow();
        }
    }

    class RemoveRowAnAction extends BaseAnAction {

        public RemoveRowAnAction() {
            super(null, () -> "Remove New Row", AllIcons.General.Remove);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            removeRow();
        }
    }

    class CopyRowAnAction extends BaseAnAction {
        public CopyRowAnAction() {
            // TODO: 2024/1/12 这个ui找不到 
            super(null, () -> "Copy Row", AllIcons.Actions.DynamicUsages);
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

        public ToolbarBuilder enabledAdd() {
            addButton = true;
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
            return this;
        }
    }
}
