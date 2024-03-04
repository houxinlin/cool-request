package com.cool.request.view.dialog;

import com.cool.request.action.actions.DynamicAnAction;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.common.state.CustomControllerFolderPersistent;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.TreePathUtils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.*;
import java.util.List;

public class CustomControllerFolderSelectDialog extends DialogWrapper {
    private final SimpleTree jTree = new SimpleTree();
    private Object selectResult;
    private Project project;

    public CustomControllerFolderSelectDialog(Project project) {
        super(project);
        this.project = project;
        setTitle("Select Save Folder");
        DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        jTree.setCellRenderer(new CustomControllerFolderTreeCellRenderer());
        jTree.setRootVisible(true);
        jTree.setShowsRootHandles(true);
        TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree.setSelectionModel(selectionModel);
        jTree.setPopupGroup(getPopupActions(), "CustomControllerFolderSelectDialog");
        CustomControllerFolderPersistent.Folder folder = CustomControllerFolderPersistent.getInstance().getFolder();
        FolderTreeNode rootNode = new FolderTreeNode(folder);
        model.setRoot(rootNode);

        buildNodeTree(rootNode, folder.getItems());
        jTree.updateUI();
        TreePathUtils.expandAll(jTree);
        init();
    }


    public void buildNodeTree(FolderTreeNode treeNode, List<CustomControllerFolderPersistent.Folder> folder) {
        for (CustomControllerFolderPersistent.Folder item : folder) {
            FolderTreeNode folderTreeNode = new FolderTreeNode(item);
            treeNode.add(folderTreeNode);
            buildNodeTree(folderTreeNode, item.getItems());
        }
    }

    protected ActionGroup getPopupActions() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new CreateNewFolderAnAction());
        group.addSeparator();
        group.add(new DeleteFolderAnAction(project));
        return group;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(jTree);
        if (selectedPathIfOne == null) return;
        if (selectedPathIfOne.getLastPathComponent() == null) return;
        this.selectResult = selectedPathIfOne.getLastPathComponent();

    }

    public Object getSelectResult() {
        return selectResult;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JBScrollPane mainJBScrollPane = new JBScrollPane();
        mainJBScrollPane.setViewportView(jTree);
        setSize(400, 400);
        return mainJBScrollPane;
    }

    public class DeleteFolderAnAction extends DynamicAnAction {
        public DeleteFolderAnAction(Project project) {
            super(project, () -> "Delete Folder", KotlinCoolRequestIcons.INSTANCE.getDELETE());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(jTree);
            if (selectedPathIfOne != null) {
                FolderTreeNode currentFolder = (FolderTreeNode) selectedPathIfOne.getLastPathComponent();
                FolderTreeNode parent = (FolderTreeNode) currentFolder.getParent();
                if (parent == null) {
                    MessagesWrapperUtils.showErrorDialog(ResourceBundleUtils.getString("cannot.delete.root.node"), ResourceBundleUtils.getString("tip"));
                    return;
                }
                ((CustomControllerFolderPersistent.Folder) parent.getUserObject())
                        .remove(((CustomControllerFolderPersistent.Folder) currentFolder.getUserObject()));
                TreeUtil.removeLastPathComponent(jTree, selectedPathIfOne);
            }
            jTree.updateUI();
        }
    }

    public class CreateNewFolderAnAction extends AnAction {
        public CreateNewFolderAnAction() {
            super(() -> "Add New Folder", AllIcons.Actions.NewFolder);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            String result = Messages.showInputDialog("Input name", ResourceBundleUtils.getString("tip"), AllIcons.Actions.Edit);
            if (!StringUtils.hasText(result)) return;
            TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(jTree);
            if (selectedPathIfOne != null) {
                FolderTreeNode folderTreeNode = (FolderTreeNode) selectedPathIfOne.getLastPathComponent();
                CustomControllerFolderPersistent.Folder newFolder = new CustomControllerFolderPersistent.Folder(result);
                ((CustomControllerFolderPersistent.Folder) folderTreeNode.getUserObject()).addSubFolder(newFolder);
                folderTreeNode.add(new FolderTreeNode(newFolder));
            }
            jTree.updateUI();
        }
    }

    public static class FolderTreeNode extends DefaultMutableTreeNode {
        public FolderTreeNode(Object userObject) {
            super(userObject);
        }
    }
}
