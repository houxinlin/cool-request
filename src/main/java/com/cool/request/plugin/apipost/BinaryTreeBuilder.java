package com.cool.request.plugin.apipost;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TreeNode {
    private List<TreeNode> children;
    private ApipostFolder treeData;

    public TreeNode(List<TreeNode> children, ApipostFolder treeData) {
        this.children = children;
        this.treeData = treeData;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public ApipostFolder getTreeData() {
        return treeData;
    }

    public void setTreeData(ApipostFolder treeData) {
        this.treeData = treeData;
    }
}

interface NodeEvent {
    public void nodeParentAdded(String id, TreeNode treeNode, NodeEventManager nodeEventManager);

    public boolean canResponse(String id, TreeNode treeNode);
}

class NodeEventImpl implements NodeEvent {
    private final ApipostFolder item;

    public NodeEventImpl(ApipostFolder data) {
        this.item = data;
    }

    @Override
    public boolean canResponse(String id, TreeNode treeNode) {
        String parentId = item.getLocalParentId();
        return id.equals(parentId);
    }

    @Override
    public void nodeParentAdded(String nodeId, TreeNode treeNode, NodeEventManager nodeEventManager) {
        String id = item.getLocalTargetId();
        String parentId = item.getLocalParentId();
        if (nodeId.equals(parentId)) {
            TreeNode newTreeNode = new TreeNode(new ArrayList<>(), item);
            treeNode.getChildren().add(newTreeNode);
            if (nodeEventManager.canResponse(id, newTreeNode)) {
                nodeEventManager.nodeParentAdded(id, newTreeNode, nodeEventManager);
            }
        }
    }
}

class NodeEventManager implements NodeEvent {
    private final List<NodeEvent> nodeEvents = new ArrayList<>();

    public void register(NodeEvent nodeEvent) {
        nodeEvents.add(nodeEvent);
    }

    @Override
    public void nodeParentAdded(String id, TreeNode treeNode, NodeEventManager nodeEventManager) {
        for (NodeEvent nodeEvent : nodeEvents) {
            nodeEvent.nodeParentAdded(id, treeNode, nodeEventManager);
        }
    }

    @Override
    public boolean canResponse(String id, TreeNode treeNode) {
        for (NodeEvent nodeEvent : nodeEvents) {
            if (nodeEvent.canResponse(id, treeNode)) return true;
        }
        return false;
    }
}

public class BinaryTreeBuilder {
    private final TreeNode root = new TreeNode(new ArrayList<>(), null);
    private final NodeEventManager nodeEvents = new NodeEventManager();
    private final Map<String, TreeNode> mapNode = new HashMap<>();

    public TreeNode buildTree(List<ApipostFolder> jsonData) {
        if (jsonData == null || jsonData.isEmpty()) {
            return null;
        }

        for (ApipostFolder item : jsonData) {
            String id = item.getLocalTargetId();
            String parentId = item.getLocalParentId();
            if ("0".equals(parentId)) {
                TreeNode treeNode = new TreeNode(new ArrayList<>(), item);
                root.getChildren().add(treeNode);
                mapNode.put(id, treeNode);
                if (nodeEvents.canResponse(id, treeNode)) {
                    nodeEvents.nodeParentAdded(id, treeNode, nodeEvents);
                }
            } else {
                if (mapNode.containsKey(parentId)) {
                    TreeNode treeNode = new TreeNode(new ArrayList<>(), item);
                    mapNode.get(parentId).getChildren().add(treeNode);
                    mapNode.put(id, treeNode);
                    if (nodeEvents.canResponse(id, treeNode)) {
                        nodeEvents.nodeParentAdded(id, treeNode, nodeEvents);
                    }
                } else {
                    nodeEvents.register(new NodeEventImpl(item));
                }
            }
        }
        return root;
    }
}
