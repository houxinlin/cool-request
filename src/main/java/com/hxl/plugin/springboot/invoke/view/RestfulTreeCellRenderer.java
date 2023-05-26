package com.hxl.plugin.springboot.invoke.view;


import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpoint;
import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTreeCellRenderer;
import icons.MyIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class RestfulTreeCellRenderer extends ColoredTreeCellRenderer {

    @Override
    public void customizeCellRenderer(
            @NotNull JTree tree, Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row, boolean hasFocus) {
        if (value instanceof TopTreeView.ScheduledMethodNode) {
            TopTreeView.ScheduledMethodNode node = (TopTreeView.ScheduledMethodNode) value;
            setIcon(AllIcons.Actions.Execute);
            append(node.getData().getMethodName());
        }else if (value instanceof TopTreeView.ModuleNode) {
            TopTreeView.ModuleNode node = (TopTreeView.ModuleNode) value;
            setIcon(AllIcons.Modules.SourceRoot);
            append(node.getData());
        }else if (value instanceof TopTreeView.ClassNameNode) {
            TopTreeView.TreeNode<?> node = (TopTreeView.TreeNode<?>) value;
            setIcon(AllIcons.FileTypes.Java);
            append(node.toString());
        } else if (value instanceof TopTreeView.RequestMappingNode) {
            TopTreeView.RequestMappingNode node = (TopTreeView.RequestMappingNode) value;
            SpringMvcRequestMappingEndpoint springMvcRequestMappingEndpoint = node.getData().getSpringMvcRequestMappingEndpoint();
            switch (springMvcRequestMappingEndpoint.getHttpMethod()) {
                case "GET":
                    setIcon(MyIcons.GET_METHOD);
                    break;
                case "POST":
                    setIcon(MyIcons.POST_METHOD);
                    break;
                case "DELETE":
                    setIcon(MyIcons.DELTE_METHOD);
                    break;
                case "PUT":
                    setIcon(MyIcons.PUT_METHOD);
                    break;
            }
            append(springMvcRequestMappingEndpoint.getUrl());
        } else if (value instanceof TopTreeView.TreeNode<?>) {
            TopTreeView.TreeNode<?> node = (TopTreeView.TreeNode<?>) value;
            append(node.toString());
        }
    }

}