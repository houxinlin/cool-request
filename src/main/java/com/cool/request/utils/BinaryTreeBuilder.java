
/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BinaryTreeBuilder.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


interface NodeEvent<T> {
    public void nodeParentAdded(String id, TreeNode<T> treeNode, NodeEventManager<T> nodeEventManager);

    public boolean canResponse(String id, TreeNode<T> treeNode);
}

class NodeEventImpl<T> implements NodeEvent<T> {
    private final T item;
    private final Function<T, String> selfIdFunction;
    private final Function<T, String> parentIdFunction;

    public NodeEventImpl(T data, Function<T, String> selfIdFunction, Function<T, String> parentIdFunction) {
        this.item = data;
        this.selfIdFunction = selfIdFunction;
        this.parentIdFunction = parentIdFunction;
    }

    @Override
    public boolean canResponse(String id, TreeNode<T> treeNode) {
        String parentId = parentIdFunction.apply(item);
        return id.equals(parentId);
    }

    @Override
    public void nodeParentAdded(String nodeId, TreeNode<T> treeNode, NodeEventManager<T> nodeEventManager) {
        String id = selfIdFunction.apply(item);
        String parentId = parentIdFunction.apply(item);
        if (nodeId.equals(parentId)) {
            TreeNode<T> newTreeNode = new TreeNode<>(new ArrayList<>(), item);
            treeNode.getChildren().add(newTreeNode);
            if (nodeEventManager.canResponse(id, newTreeNode)) {
                nodeEventManager.nodeParentAdded(id, newTreeNode, nodeEventManager);
            }
        }
    }
}

class NodeEventManager<T> implements NodeEvent<T> {
    private final List<NodeEvent<T>> nodeEvents = new ArrayList<>();

    public void register(NodeEvent<T> nodeEvent) {
        nodeEvents.add(nodeEvent);
    }

    @Override
    public void nodeParentAdded(String id, TreeNode<T> treeNode, NodeEventManager<T> nodeEventManager) {
        for (NodeEvent<T> nodeEvent : nodeEvents) {
            nodeEvent.nodeParentAdded(id, treeNode, nodeEventManager);
        }
    }

    @Override
    public boolean canResponse(String id, TreeNode<T> treeNode) {
        for (NodeEvent<T> nodeEvent : nodeEvents) {
            if (nodeEvent.canResponse(id, treeNode)) return true;
        }
        return false;
    }
}

public class BinaryTreeBuilder<T> {
    private final TreeNode<T> root = new TreeNode<T>(new ArrayList<>(), null);
    private final NodeEventManager<T> nodeEvents = new NodeEventManager<>();
    private final Map<String, TreeNode<T>> mapNode = new HashMap<>();

    public TreeNode<T> buildTree(List<T> data, Function<T, String> selfIdFunction, Function<T, String> parentIdFunction) {
        if (data == null || data.isEmpty()) {
            return null;
        }

        for (T item : data) {
            String id = selfIdFunction.apply(item);
            String parentId = parentIdFunction.apply(item);
            if ("0".equals(parentId)) {
                TreeNode<T> treeNode = new TreeNode<>(new ArrayList<>(), item);
                root.getChildren().add(treeNode);
                mapNode.put(id, treeNode);
                if (nodeEvents.canResponse(id, treeNode)) {
                    nodeEvents.nodeParentAdded(id, treeNode, nodeEvents);
                }
            } else {
                if (mapNode.containsKey(parentId)) {
                    TreeNode<T> treeNode = new TreeNode<>(new ArrayList<>(), item);
                    mapNode.get(parentId).getChildren().add(treeNode);
                    mapNode.put(id, treeNode);
                    if (nodeEvents.canResponse(id, treeNode)) {
                        nodeEvents.nodeParentAdded(id, treeNode, nodeEvents);
                    }
                } else {
                    nodeEvents.register(new NodeEventImpl<>(item, selfIdFunction, parentIdFunction));
                }
            }
        }
        return root;
    }
}