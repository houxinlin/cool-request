package com.cool.request.plugin.apipost;

import com.cool.request.common.icons.CoolRequestIcons;
import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ApipostColoredTreeCellRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof ApipostProjectFolderSelectDialog.TeamTreeNode) {
            ApipostProjectFolderSelectDialog.TeamTreeNode node = (ApipostProjectFolderSelectDialog.TeamTreeNode) value;
            append(node.getTeam().getName());
            setIcon(CoolRequestIcons.TEAM);
        } else if (value instanceof ApipostProjectFolderSelectDialog.ProjectTreeNode) {
            ApipostProjectFolderSelectDialog.ProjectTreeNode node = (ApipostProjectFolderSelectDialog.ProjectTreeNode) value;
            setIcon(AllIcons.Actions.ProjectDirectory);
            append(node.getProject().getName());
        } else if (value instanceof ApipostProjectFolderSelectDialog.FolderTreeNode) {
            ApipostProjectFolderSelectDialog.FolderTreeNode node = (ApipostProjectFolderSelectDialog.FolderTreeNode) value;
            appendTextPadding(10);
            setIcon(AllIcons.Nodes.Folder);
            append(node.getApipostFolder().getName());
        }
    }
}
