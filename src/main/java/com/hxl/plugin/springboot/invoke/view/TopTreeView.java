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

        int size = mvcRequestMappingEndpoints.size();
        RootNode root = new RootNode((size + "个映射"));
        for (String className : requestMap.keySet()) {
            PackageNode moduleNode = new PackageNode(className);
            for (SpringMvcRequestMappingEndpoint springMvcRequestMappingEndpoint : requestMap.get(className)) {
                moduleNode.add(new RequestMappingNode(new SpringMvcRequestMappingEndpointPlus(servletContextPath, serverPort, springMvcRequestMappingEndpoint), serverPort, servletContextPath));
            }
            root.add(moduleNode);
        }
        System.out.println("set root");
        ((DefaultTreeModel) tree.getModel()).setRoot(root);
    }


    public TopTreeView() {
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (lastSelectedPathComponent == null) return;
            Object userObject = lastSelectedPathComponent.getUserObject();
            if (userObject instanceof SpringMvcRequestMappingEndpointPlus) {
                for (RequestMappingSelectedListener requestMappingSelectedListener : requestMappingSelectedListeners) {
                    requestMappingSelectedListener.selectRequestMappingSelectedEvent((SpringMvcRequestMappingEndpointPlus) userObject);
                }
            }
        });
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(new DefaultMutableTreeNode());
        tree.setCellRenderer(new RestfulTreeCellRenderer());
        tree.setRootVisible(true);
        tree.setShowsRootHandles(false);
        this.setViewportView(tree);
    }

    /**
     * 包名
     */
    public static class PackageNode extends TreeNode<String> {

        public PackageNode(String data) {
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
