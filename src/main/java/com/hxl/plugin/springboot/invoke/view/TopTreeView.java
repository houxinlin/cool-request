package com.hxl.plugin.springboot.invoke.view;

import com.hxl.plugin.springboot.invoke.bean.SpringBootScheduledEndpoint;
import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpoint;
import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpointPlus;
import com.hxl.plugin.springboot.invoke.listener.EndpointListener;
import com.hxl.plugin.springboot.invoke.listener.RequestMappingSelectedListener;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.*;

public class TopTreeView extends JBScrollPane implements EndpointListener {
    private final Tree tree = new SimpleTree();
    private final List<RequestMappingSelectedListener> requestMappingSelectedListeners = new ArrayList<>();

    public void registerRequestMappingSelected(RequestMappingSelectedListener requestMappingSelectedListener) {
        this.requestMappingSelectedListeners.add(requestMappingSelectedListener);
    }

    @Override
    public void onEndpoint(int serverPort, String servletContextPath, Set<SpringMvcRequestMappingEndpoint> mvcRequestMappingEndpoints,
                           Set<SpringBootScheduledEndpoint> scheduledEndpoints) {
        Map<String, List<SpringMvcRequestMappingEndpoint>> requestMap = new HashMap<>();
        for (SpringMvcRequestMappingEndpoint springMvcRequestMappingEndpoint : mvcRequestMappingEndpoints) {
            List<SpringMvcRequestMappingEndpoint> springMvcRequestMappingEndpoints = requestMap.computeIfAbsent(springMvcRequestMappingEndpoint.getSimpleClassName(), s -> new ArrayList<>());
            springMvcRequestMappingEndpoints.add(springMvcRequestMappingEndpoint);
        }

        ModuleNode controller = new ModuleNode("Controller");
        ModuleNode scheduled = new ModuleNode("Scheduled");
        int size = mvcRequestMappingEndpoints.size();
        RootNode root = new RootNode((size + " mapper"));
        for (String className : requestMap.keySet()) {
            ClassNameNode moduleNode = new ClassNameNode(className);
            for (SpringMvcRequestMappingEndpoint springMvcRequestMappingEndpoint : requestMap.get(className)) {
                moduleNode.add(new RequestMappingNode(new SpringMvcRequestMappingEndpointPlus(servletContextPath, serverPort, springMvcRequestMappingEndpoint), serverPort, servletContextPath));
            }
            controller.add(moduleNode);
        }
        Map<String, List<SpringBootScheduledEndpoint>> scheduleClassName = new HashMap<>();
        for (SpringBootScheduledEndpoint scheduledEndpoint : scheduledEndpoints) {
            List<SpringBootScheduledEndpoint> springBootScheduledEndpoints = scheduleClassName.computeIfAbsent(scheduledEndpoint.getClassName(), s -> new ArrayList<>());
            springBootScheduledEndpoints.add(scheduledEndpoint);
        }
        for (String className : scheduleClassName.keySet()) {
            ClassNameNode moduleNode = new ClassNameNode(className);
            List<SpringBootScheduledEndpoint> springBootScheduledEndpoints = scheduleClassName.get(className);
            if (springBootScheduledEndpoints == null) continue;
            for (SpringBootScheduledEndpoint springBootScheduledEndpoint : springBootScheduledEndpoints) {
                moduleNode.add(new ScheduledMethodNode(springBootScheduledEndpoint));
            }
            scheduled.add(moduleNode);
        }

        root.add(controller);
        root.add(scheduled);
        ((DefaultTreeModel) tree.getModel()).setRoot(root);
    }


    public TopTreeView(PluginWindowView pluginWindowView) {
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (lastSelectedPathComponent == null) return;
            Object userObject = lastSelectedPathComponent.getUserObject();
            if (userObject instanceof SpringMvcRequestMappingEndpointPlus) {
                for (RequestMappingSelectedListener requestMappingSelectedListener : requestMappingSelectedListeners) {
                    requestMappingSelectedListener.requestMappingSelectedEvent((SpringMvcRequestMappingEndpointPlus) userObject);
                }
            }
            if (userObject instanceof SpringBootScheduledEndpoint) {
                for (RequestMappingSelectedListener requestMappingSelectedListener : requestMappingSelectedListeners) {
                    requestMappingSelectedListener.scheduledSelectedEvent((SpringBootScheduledEndpoint) userObject);
                }
            }
            pluginWindowView.repaint();
            pluginWindowView.invalidate();
        });
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(new DefaultMutableTreeNode());
        tree.setCellRenderer(new RestfulTreeCellRenderer());
        tree.setRootVisible(true);
        tree.setShowsRootHandles(false);
        this.setViewportView(tree);
    }

    /**
     * 类名
     */
    public static class ClassNameNode extends TreeNode<String> {

        public ClassNameNode(String data) {
            super(data);
        }
    }

    /**
     * 调度器
     */
    public static class ScheduledMethodNode extends TreeNode<SpringBootScheduledEndpoint> {
        public ScheduledMethodNode(SpringBootScheduledEndpoint data) {
            super(data);
        }
    }

    /**
     * 请求方法信息
     */
    public static class RequestMappingNode extends TreeNode<SpringMvcRequestMappingEndpointPlus> {
        private int serverPort;
        private String servletContextPath;

        public RequestMappingNode(SpringMvcRequestMappingEndpointPlus data, int serverPort, String servletContextPath) {
            super(data);
            this.serverPort = serverPort;
            this.servletContextPath = servletContextPath;
        }
    }

    /**
     * 模块
     */
    public static class ModuleNode extends TreeNode<String> {
        public ModuleNode(String data) {
            super(data);
        }
    }

    /**
     * root
     */
    public static class RootNode extends TreeNode<String> {
        public RootNode(String data) {
            super(data);
        }
    }

    public static abstract class TreeNode<T> extends DefaultMutableTreeNode {
        private final T data;

        public TreeNode(T data) {
            super(data);
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }
}
