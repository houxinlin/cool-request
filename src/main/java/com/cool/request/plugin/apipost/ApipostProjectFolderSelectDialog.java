package com.cool.request.plugin.apipost;

import com.cool.request.utils.*;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

public class ApipostProjectFolderSelectDialog extends DialogWrapper implements TreeExpansionListener {
    private final SimpleTree jTree = new SimpleTree();
    private final ApipostAPI apipostAPI = new ApipostAPI();
    private final Project project;
    private final SelectCallback selectCallback;

    public ApipostProjectFolderSelectDialog(@Nullable Project project, SelectCallback selectCallback) {
        super(project);
        this.project = project;
        this.selectCallback = selectCallback;
        DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        model.setRoot(new DefaultMutableTreeNode());
        jTree.setCellRenderer(new ApipostColoredTreeCellRenderer());
        jTree.setRootVisible(false);
        jTree.setShowsRootHandles(true);
        TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree.setSelectionModel(selectionModel);
        jTree.addTreeExpansionListener(this);
        jTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                showPopupMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                showPopupMenu(e);
            }
        });

        setTitle("Apipost");
        init();
    }

    public static interface SelectCallback {
        public void selectNode(String project, String folderId);
    }


    @Override
    protected void doOKAction() {

        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(jTree);
        if (selectedPathIfOne != null) {
            Object lastPathComponent = selectedPathIfOne.getLastPathComponent();
            if (lastPathComponent instanceof FolderTreeNode) {
                selectCallback.selectNode(getProjectId(lastPathComponent),
                        ((FolderTreeNode) lastPathComponent).getApipostFolder().getLocalTargetId());
                super.doOKAction();
                return;
            }
            MessagesWrapperUtils.showErrorDialogWithI18n("please.select.folder");
        }
    }

    public static void showDialog(Project project, SelectCallback selectCallback) {
        new ApipostProjectFolderSelectDialog(project, selectCallback).show();
    }

    private ActionGroup getActionGroup(Object lastPathComponent) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        if (lastPathComponent instanceof ProjectTreeNode || lastPathComponent instanceof FolderTreeNode) {
            actionGroup.add(new CreateNewFolderAnAction(lastPathComponent));
            return actionGroup;
        }
        return null;
    }

    private void showPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger() && insideTreeItemsArea(e)) {
            TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(jTree);
            if (selectedPathIfOne == null) return;
            Object lastPathComponent = selectedPathIfOne.getLastPathComponent();
            ActionGroup actionGroup = getActionGroup(lastPathComponent);
            if (actionGroup == null) return;
            JPopupMenu component = ActionManager.getInstance()
                    .createActionPopupMenu("ApipostProjectFolderSelectDialog", actionGroup)
                    .getComponent();
            component.show(e.getComponent(), e.getX(), e.getY());
        }

    }

    private boolean insideTreeItemsArea(MouseEvent e) {
        Rectangle rowBounds = jTree.getRowBounds(jTree.getRowCount() - 1);
        if (rowBounds == null) {
            return false;
        }
        double lastItemBottomLine = rowBounds.getMaxY();
        return e.getY() <= lastItemBottomLine;
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {

    }

    private void loadProjectTree() {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    ApipostProjectResponse apipostProjectResponse = apipostAPI.listProject();
                    SwingUtilities.invokeLater(() -> buildProjectTree(apipostProjectResponse));
                } catch (IOException e) {
                    ExceptionDialogHandlerUtils.handlerException(e);
                }
            }
        });
    }

    private void loadFolder(String projectId, ProjectTreeNode projectTreeNode) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    ApipostFolderResponse apipostFolderResponse = apipostAPI.listFolder(projectId);
                    SwingUtilities.invokeLater(() -> buildFolderTree(projectTreeNode, apipostFolderResponse.getData()));
                } catch (IOException e) {
                    ExceptionDialogHandlerUtils.handlerException(e);
                }

            }
        });
    }

    private void buildFolderTree(ProjectTreeNode projectTreeNode, List<ApipostFolder> apipostFolder) {
        TreeNode treeNode = new BinaryTreeBuilder().buildTree(apipostFolder);
        for (TreeNode child : treeNode.getChildren()) {
            FolderTreeNode folderTreeNode = new FolderTreeNode(child.getTreeData());
            projectTreeNode.add(folderTreeNode);
            ((DefaultTreeModel) jTree.getModel()).reload(folderTreeNode);
            buildFolderChildren(folderTreeNode, child.getChildren());
        }
    }

    private void buildFolderChildren(FolderTreeNode parentTreeNode, List<TreeNode> treeNodes) {
        for (TreeNode treeNode : treeNodes) {
            FolderTreeNode subFolder = new FolderTreeNode(treeNode.getTreeData());
            parentTreeNode.add(subFolder);
            ((DefaultTreeModel) jTree.getModel()).reload(subFolder);
            buildFolderChildren(subFolder, treeNode.getChildren());
        }
    }

    private void buildProjectTree(ApipostProjectResponse apipostProjectResponse) {
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) jTree.getModel().getRoot();
        for (ApipostProjectResponse.TeamDTO teamDTO : apipostProjectResponse.getData().getList()) {
            TeamTreeNode treeNode = new TeamTreeNode(teamDTO);
            defaultMutableTreeNode.add(treeNode);
            for (ApipostProjectResponse.ProjectDTO projectDTO : teamDTO.getProject()) {
                ProjectTreeNode projectTreeNode = new ProjectTreeNode(projectDTO);
                treeNode.add(projectTreeNode);
                loadFolder(projectDTO.getProjectId(), projectTreeNode);
            }
        }
        ((DefaultTreeModel) jTree.getModel()).reload();

    }

    private String getProjectId(Object lastPathComponent) {
        if (lastPathComponent instanceof ProjectTreeNode)
            return ((ProjectTreeNode) lastPathComponent).getProject().getProjectId();
        if (lastPathComponent instanceof FolderTreeNode) {
            javax.swing.tree.TreeNode folderTreeNode = (javax.swing.tree.TreeNode) lastPathComponent;
            while (folderTreeNode.getParent() != null) {
                if (folderTreeNode.getParent() instanceof ProjectTreeNode) {
                    return ((ProjectTreeNode) folderTreeNode.getParent()).getProject().getProjectId();
                } else {
                    folderTreeNode = folderTreeNode.getParent();
                }
            }
        }
        return "0";
    }


    public class CreateNewFolderAnAction extends AnAction {
        private final Object lastPathComponent;


        public CreateNewFolderAnAction(Object lastPathComponent) {
            super(() -> "Add New Folder", AllIcons.Actions.NewFolder);
            this.lastPathComponent = lastPathComponent;
        }


        private String getTargetId() {
            if (lastPathComponent instanceof FolderTreeNode) {
                return ((FolderTreeNode) lastPathComponent).getApipostFolder().getLocalTargetId();
            }
            return "";
        }

        private ApipostFolder buildNewApipostFolder(ApipostCreateFolderResponse folderResponse) {
            ApipostFolder apipostFolder = new ApipostFolder();
            apipostFolder.setName(folderResponse.getData().get(0).getFolder());
            apipostFolder.setLocalTargetId(folderResponse.getData().get(0).getTargetId());
            if (lastPathComponent instanceof FolderTreeNode) {
                apipostFolder.setLocalParentId(((FolderTreeNode) lastPathComponent).apipostFolder.getLocalParentId());
            } else {
                apipostFolder.setLocalParentId("0");
            }
            return apipostFolder;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            String result = Messages.showInputDialog("Input name", ResourceBundleUtils.getString("tip"), AllIcons.Actions.Edit);
            if (!StringUtils.hasText(result)) return;
            ProgressManager.getInstance().run(new Task.Backgroundable(project, "") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    ApipostCreateFolderRequestBody apipostCreateFolderRequestBody = new ApipostCreateFolderRequestBody();
                    apipostCreateFolderRequestBody.setFolder(List.of(result));
                    apipostCreateFolderRequestBody.setProjectId(getProjectId(lastPathComponent));
                    apipostCreateFolderRequestBody.setTargetId(getTargetId());
                    ApipostCreateFolderResponse folder = null;
                    try {
                        folder = apipostAPI.createFolder(apipostCreateFolderRequestBody);
                        if (folder != null && folder.getCode().equals(10000)) {
                            if (!folder.getData().isEmpty()) {
                                FolderTreeNode folderTreeNode = new FolderTreeNode(buildNewApipostFolder(folder));
                                ((DefaultMutableTreeNode) lastPathComponent).add(folderTreeNode);
                                SwingUtilities.invokeLater(() -> {
                                    ((DefaultTreeModel) jTree.getModel()).reload(((DefaultMutableTreeNode) lastPathComponent));
                                    ((DefaultTreeModel) jTree.getModel()).nodeChanged(((DefaultMutableTreeNode) lastPathComponent));
                                });
                                if (!(lastPathComponent instanceof FolderTreeNode)) return;
                                if (((DefaultMutableTreeNode) lastPathComponent).getChildCount() == 1) {
                                    SwingUtilities.invokeLater(() -> {
                                        TreePath path = new TreePath(((DefaultMutableTreeNode) lastPathComponent).getPath());
                                        TreePathUtils.expandPath(jTree, path);
                                    });
                                }
                            }
                        }
                    } catch (IOException ex) {
                        ExceptionDialogHandlerUtils.handlerException(ex);
                    }

                }
            });
        }
    }

    public static class TeamTreeNode extends DefaultMutableTreeNode {
        private final ApipostProjectResponse.TeamDTO teamDTO;

        public TeamTreeNode(ApipostProjectResponse.TeamDTO teamDTO) {
            this.teamDTO = teamDTO;
        }

        public ApipostProjectResponse.TeamDTO getTeam() {
            return teamDTO;
        }
    }

    public static class ProjectTreeNode extends DefaultMutableTreeNode {
        private final ApipostProjectResponse.ProjectDTO projectDTO;

        public ProjectTreeNode(ApipostProjectResponse.ProjectDTO projectDTO) {
            super(projectDTO);
            this.projectDTO = projectDTO;
        }

        public ApipostProjectResponse.ProjectDTO getProject() {
            return projectDTO;
        }
    }

    public static class FolderTreeNode extends DefaultMutableTreeNode {
        private final ApipostFolder apipostFolder;

        public FolderTreeNode(ApipostFolder apipostFolder) {
            this.apipostFolder = apipostFolder;
        }

        public ApipostFolder getApipostFolder() {
            return apipostFolder;
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JBScrollPane mainJBScrollPane = new JBScrollPane();
        mainJBScrollPane.setViewportView(jTree);
        setSize(400, 400);
        loadProjectTree();
        return mainJBScrollPane;
    }


}
