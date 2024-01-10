package com.hxl.plugin.springboot.invoke.view;


import com.hxl.plugin.springboot.invoke.bean.components.DynamicComponent;
import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.model.SpringMvcRequestMappingSpringInvokeEndpoint;
import com.hxl.plugin.springboot.invoke.utils.StringUtils;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
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
        if (value instanceof MainTopTreeView.ScheduledMethodNode) {
            MainTopTreeView.ScheduledMethodNode node = (MainTopTreeView.ScheduledMethodNode) value;
            setIcon(AllIcons.Actions.Execute);
            append(node.getData().getMethodName());
        } else if (value instanceof MainTopTreeView.FeaturesModuleNode) {
            MainTopTreeView.FeaturesModuleNode node = (MainTopTreeView.FeaturesModuleNode) value;
            setIcon(AllIcons.Nodes.ModuleGroup);
            append(node.getData());
        } else if (value instanceof MainTopTreeView.ClassNameNode) {
            MainTopTreeView.TreeNode<?> node = (MainTopTreeView.TreeNode<?>) value;
            setIcon(AllIcons.Nodes.Class);
            append(node.toString());
        } else if (value instanceof MainTopTreeView.ProjectModuleNode) {
            MainTopTreeView.TreeNode<?> node = (MainTopTreeView.TreeNode<?>) value;
            setIcon(AllIcons.Actions.ModuleDirectory);
            append(node.toString());
        } else if (value instanceof MainTopTreeView.RequestMappingNode) {
            MainTopTreeView.RequestMappingNode node = (MainTopTreeView.RequestMappingNode) value;
            Controller controller = node.getData();
            setIcon(getIcon(controller));
            append(StringUtils.getFullUrl(node.getData()));
        } else if (value instanceof MainTopTreeView.TreeNode<?>) {
            MainTopTreeView.TreeNode<?> node = (MainTopTreeView.TreeNode<?>) value;
            append(node.toString());
        }
    }

    private Icon getIconByHttpMethod(String method) {
        switch (method.toUpperCase()) {
            case "GET":
                return MyIcons.GET_METHOD;
            case "POST":
                return MyIcons.POST_METHOD;
            case "DELETE":
                return MyIcons.DELTE_METHOD;
            case "PUT":
                return MyIcons.PUT_METHOD;
        }
        return MyIcons.POST_METHOD;
    }

    private Icon getIcon(Controller controller) {
        if (controller instanceof DynamicComponent) {
            return new MergedIcon(MyIcons.LIGHTNING, getIconByHttpMethod(controller.getHttpMethod()));
        }
        return getIconByHttpMethod(controller.getHttpMethod());

    }

}