package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.bean.SpringBootScheduledEndpoint;
import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpoint;
import com.hxl.plugin.springboot.invoke.bean.SpringMvcRequestMappingEndpointPlus;
import com.hxl.plugin.springboot.invoke.listener.EndpointListener;
import com.hxl.plugin.springboot.invoke.listener.RequestMappingSelectedListener;
import com.hxl.plugin.springboot.invoke.view.PluginWindowView;
import com.hxl.plugin.springboot.invoke.view.RestfulTreeCellRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.*;

public class MainTopTreeView extends JBScrollPane implements EndpointListener {
    private final Tree tree = new SimpleTree();
    private final Project project;
    private final List<RequestMappingSelectedListener> requestMappingSelectedListeners = new ArrayList<>();
    private final Map<ClassNameNode, List<RequestMappingNode>> requestMappingNodeMap = new HashMap<>();

    private final ModuleNode controller = new ModuleNode("Controller");
    private final ModuleNode scheduled = new ModuleNode("Scheduled");
    private RootNode root = null;

    public void registerRequestMappingSelected(RequestMappingSelectedListener requestMappingSelectedListener) {
        this.requestMappingSelectedListeners.add(requestMappingSelectedListener);
    }

    public Map<ClassNameNode, List<RequestMappingNode>> getRequestMappingNodeMap() {
        return requestMappingNodeMap;
    }

    public List<RequestMappingSelectedListener> getRequestMappingSelectedListeners() {
        return requestMappingSelectedListeners;
    }
    public void selectNode( RequestMappingNode requestMappingNode) {
        for (ClassNameNode classNameNode : requestMappingNodeMap.keySet()) {
            for (RequestMappingNode value :requestMappingNodeMap.get(classNameNode)) {
                if (value ==requestMappingNode){
                    TreePath path = new TreePath(new Object[]{root, controller,classNameNode, requestMappingNode});
                    tree.getSelectionModel().setSelectionPath(path);
                    tree.scrollPathToVisible(path);
                    tree.updateUI();
                }
            }
        }
    }

    @Override
    public void onEndpoint(int serverPort, String servletContextPath, Set<SpringMvcRequestMappingEndpoint> mvcRequestMappingEndpoints,
                           Set<SpringBootScheduledEndpoint> scheduledEndpoints) {
        requestMappingNodeMap.clear();
        Map<String, List<SpringMvcRequestMappingEndpoint>> requestMap = new HashMap<>();
        for (SpringMvcRequestMappingEndpoint springMvcRequestMappingEndpoint : mvcRequestMappingEndpoints) {
            List<SpringMvcRequestMappingEndpoint> springMvcRequestMappingEndpoints = requestMap.computeIfAbsent(springMvcRequestMappingEndpoint.getSimpleClassName(), s -> new ArrayList<>());
            springMvcRequestMappingEndpoints.add(springMvcRequestMappingEndpoint);
        }
        int size = mvcRequestMappingEndpoints.size();
        root = new RootNode((size + " mapper"));
        for (String className : requestMap.keySet()) {
            ClassNameNode moduleNode = new ClassNameNode(className);
            requestMappingNodeMap.put(moduleNode, new ArrayList<>());
            for (SpringMvcRequestMappingEndpoint springMvcRequestMappingEndpoint : requestMap.get(className)) {
                SpringMvcRequestMappingEndpointPlus springMvcRequestMappingEndpointPlus = new SpringMvcRequestMappingEndpointPlus(servletContextPath, serverPort, springMvcRequestMappingEndpoint);
                RequestMappingNode requestMappingNode = new RequestMappingNode(springMvcRequestMappingEndpointPlus, serverPort, servletContextPath);
                requestMappingNodeMap.get(moduleNode).add(requestMappingNode);
                moduleNode.add(requestMappingNode);
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

    private PsiClass findClassByName(Project project, String fullClassName) {
        String[] classNameParts = fullClassName.split("\\.");
        String className = classNameParts[classNameParts.length - 1];
        @NotNull PsiClass[] items = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        for (PsiClass item : items) {
            String qualifiedName = item.getQualifiedName();
            if (qualifiedName.equals(fullClassName)) return item;
        }
        return null;
    }

    private PsiMethod findMethodInClass(PsiClass psiClass, String methodName) {
        for (PsiMethod method : psiClass.getAllMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }


    private void navigate(SpringMvcRequestMappingEndpointPlus springMvcRequestMappingEndpointPlus) {
        PsiClass psiClass = findClassByName(project, springMvcRequestMappingEndpointPlus.getSpringMvcRequestMappingEndpoint().getSimpleClassName());
        if (psiClass != null) {
            PsiMethod methodInClass = findMethodInClass(psiClass, springMvcRequestMappingEndpointPlus.getSpringMvcRequestMappingEndpoint().getMethodName());
            if (methodInClass != null) methodInClass.navigate(true);
        }
    }


    public MainTopTreeView(Project project, PluginWindowView pluginWindowView) {
        this.project = project;
        //设置点击事件
        savedTreeState = TreeUtil.createExpandedState(simpleTree);
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (lastSelectedPathComponent == null) return;
            Object userObject = lastSelectedPathComponent.getUserObject();
            if (userObject instanceof SpringMvcRequestMappingEndpointPlus) {
                for (RequestMappingSelectedListener requestMappingSelectedListener : requestMappingSelectedListeners) {
                    SpringMvcRequestMappingEndpointPlus springMvcRequestMappingEndpointPlus = (SpringMvcRequestMappingEndpointPlus) userObject;
                    navigate(springMvcRequestMappingEndpointPlus);
                    requestMappingSelectedListener.requestMappingSelectedEvent(springMvcRequestMappingEndpointPlus);
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
