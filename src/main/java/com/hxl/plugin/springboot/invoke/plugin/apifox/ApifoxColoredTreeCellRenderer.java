package com.hxl.plugin.springboot.invoke.plugin.apifox;

import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ApifoxColoredTreeCellRenderer  extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof ApifoxProjectFolderSelectDialog.TeamTreeNode) {
            ApifoxProjectFolderSelectDialog.TeamTreeNode node = (ApifoxProjectFolderSelectDialog.TeamTreeNode) value;
            append(node.getTeam().getName());
            setIcon(AllIcons.CodeWithMe.CwmAccess);
        }else if (value instanceof ApifoxProjectFolderSelectDialog.ProjectTreeNode){
            ApifoxProjectFolderSelectDialog.ProjectTreeNode node = (ApifoxProjectFolderSelectDialog.ProjectTreeNode) value;
            setIcon(AllIcons.Actions.ProjectDirectory);
            append(node.getProject().getName());
        }else if (value instanceof ApifoxProjectFolderSelectDialog.FolderTreeNode){
            ApifoxProjectFolderSelectDialog.FolderTreeNode node = (ApifoxProjectFolderSelectDialog.FolderTreeNode) value;
            appendTextPadding(10);
            setIcon(AllIcons.Nodes.Folder);
            append(node.getFolder().getName());
        }
    }
}
