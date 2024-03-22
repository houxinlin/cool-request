package com.cool.request.utils;

import java.util.List;

public class TreeNode<T> {
    private List<TreeNode<T>> children;
    private T treeData;

    public TreeNode(List<TreeNode<T>> children, T treeData) {
        this.children = children;
        this.treeData = treeData;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode<T>> children) {
        this.children = children;
    }

    public T getTreeData() {
        return treeData;
    }

    public void setTreeData(T treeData) {
        this.treeData = treeData;
    }
}
