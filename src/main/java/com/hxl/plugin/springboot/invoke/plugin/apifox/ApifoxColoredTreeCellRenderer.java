package com.hxl.plugin.springboot.invoke.plugin.apifox;

import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ApifoxColoredTreeCellRenderer  extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof ProjectDialog.TeamTreeNode) {
            ProjectDialog.TeamTreeNode node = (ProjectDialog.TeamTreeNode) value;
            append(node.getTeam().getName());
            setIcon(AllIcons.CodeWithMe.CwmAccess);
        }else if (value instanceof ProjectDialog.ProjectTreeNode){
            ProjectDialog.ProjectTreeNode node = (ProjectDialog.ProjectTreeNode) value;
            setIcon(AllIcons.Actions.ProjectDirectory);
            append(node.getProject().getName());
        }else if (value instanceof ProjectDialog.FolderTreeNode){
            ProjectDialog.FolderTreeNode node = (ProjectDialog.FolderTreeNode) value;
            appendTextPadding(10);
            setIcon(AllIcons.Nodes.Folder);
            append(node.getFolder().getName());
        }
    }
}
