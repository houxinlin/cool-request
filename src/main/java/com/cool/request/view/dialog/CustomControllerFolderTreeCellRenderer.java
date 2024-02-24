package com.cool.request.view.dialog;

import com.cool.request.common.state.CustomControllerFolderPersistent;
import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CustomControllerFolderTreeCellRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof CustomControllerFolderSelectDialog.FolderTreeNode) {
            CustomControllerFolderSelectDialog.FolderTreeNode folderTreeNode = (CustomControllerFolderSelectDialog.FolderTreeNode) value;
            Object userObject = folderTreeNode.getUserObject();
            if (userObject instanceof CustomControllerFolderPersistent.Folder) {
                append(((CustomControllerFolderPersistent.Folder) userObject).getName());
            }
            setIcon(AllIcons.Nodes.Folder);
        }
    }
}
