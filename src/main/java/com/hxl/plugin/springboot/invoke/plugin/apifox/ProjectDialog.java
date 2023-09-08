package com.hxl.plugin.springboot.invoke.plugin.apifox;

import com.hxl.plugin.springboot.invoke.utils.CursorUtils;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ProjectDialog extends DialogWrapper implements TreeExpansionListener {
    private final JTree jTree = new SimpleTree();
    private final List<ProjectTreeNode> projectTreeNodes = new ArrayList<>();
    private final List<FolderTreeNode> folderTreeNodes = new ArrayList<>();
    private final Map<TeamTreeNode, Boolean> folderGetStateCache = new HashMap<>();
    private final ApifoxAPI apifoxAPI;

    public ProjectDialog(ApifoxAPI apifoxAPI) {
        super(ProjectManager.getInstance().getOpenProjects()[0]);
        this.apifoxAPI = apifoxAPI;
        setTitle("选择项目");
        DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        model.setRoot(new DefaultMutableTreeNode());
        jTree.setCellRenderer(new ApifoxColoredTreeCellRenderer());
        jTree.setRootVisible(false);
        jTree.setShowsRootHandles(true);
        init();

    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

    public ApifoxFolder.Folder getResult() {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(jTree);
        if (selectedPathIfOne != null && selectedPathIfOne.getLastPathComponent() instanceof FolderTreeNode) {
            return ((FolderTreeNode) selectedPathIfOne.getLastPathComponent()).getFolder();
        }
        return null;
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        Object lastPathComponent = event.getPath().getLastPathComponent();
        if (lastPathComponent instanceof ProjectDialog.TeamTreeNode) {
            if (!folderGetStateCache.getOrDefault(lastPathComponent, false)) {
                CursorUtils.setWait(jTree);
                for (ApifoxProject.Project project : ((TeamTreeNode) lastPathComponent).getProject()) {
                    ApifoxFolder apifoxFolder = apifoxAPI.listFolder(project.getId());
                    CursorUtils.setDefault(jTree);
                    folderGetStateCache.put(((TeamTreeNode) lastPathComponent), true);
                    ProjectTreeNode projectTreeNode = getProjectTreeNodeById(project.getId());//項目
                    if (projectTreeNode != null) loadFolder(apifoxFolder, projectTreeNode);
                }
            }
        }
    }

    private void loadFolder(ApifoxFolder apifoxFolder, ProjectTreeNode projectTreeNode) {
        for (ApifoxFolder.Folder datum : apifoxFolder.getData()) {
            FolderTreeNode folderTreeNode = new FolderTreeNode(datum);
            folderTreeNodes.add(folderTreeNode);
            if (datum.getParentId() == 0) {
                projectTreeNode.add(folderTreeNode);
            } else {
                FolderTreeNode treeNode = getFolderTreeNodeById(datum.getParentId());
                if (treeNode != null) treeNode.add(folderTreeNode);
            }
        }
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {

    }

    private FolderTreeNode getFolderTreeNodeById(int folderId) {
        for (FolderTreeNode folderTreeNode : folderTreeNodes) {
            if (folderTreeNode.getFolder().getId() == folderId) return folderTreeNode;
        }
        return null;
    }

    private ProjectTreeNode getProjectTreeNodeById(int projectId) {
        for (ProjectTreeNode projectTreeNode : projectTreeNodes) {
            if (projectTreeNode.getProject().getId() == projectId) return projectTreeNode;
        }
        return null;
    }

    public static void showDialog(ApifoxAPI apifoxAPI, Consumer<ApifoxFolder.Folder> consumer) {
        ProjectDialog projectDialog = new ProjectDialog(apifoxAPI);
        projectDialog.show();
        if (projectDialog.isOK()) {
            consumer.accept(projectDialog.getResult());
        }
    }

    public static class FolderTreeNode extends DefaultMutableTreeNode {
        private final ApifoxFolder.Folder folder;

        public FolderTreeNode(ApifoxFolder.Folder folder) {
            super(folder);
            this.folder = folder;
        }

        public ApifoxFolder.Folder getFolder() {
            return folder;
        }
    }

    public static class ProjectTreeNode extends DefaultMutableTreeNode {
        private ApifoxProject.Project project;

        public ProjectTreeNode(ApifoxTeam.Team team, ApifoxProject.Project project) {
            super(true);
            this.project = project;
        }

        public ApifoxProject.Project getProject() {
            return project;
        }

        public void setProject(ApifoxProject.Project project) {
            this.project = project;
        }
    }

    public static class TeamTreeNode extends DefaultMutableTreeNode {
        private final ApifoxTeam.Team team;
        private List<ApifoxProject.Project> project;

        public TeamTreeNode(ApifoxTeam.Team team, List<ApifoxProject.Project> project) {
            super(true);
            this.team = team;
            this.project = project;
        }

        public List<ApifoxProject.Project> getProject() {
            return project;
        }

        public void setProject(List<ApifoxProject.Project> project) {
            this.project = project;
        }

        public ApifoxTeam.Team getTeam() {
            return team;
        }

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JBScrollPane mainJBScrollPane = new JBScrollPane();
        mainJBScrollPane.setViewportView(jTree);
        setSize(400, 400);
        new Thread(() -> {
            try {
                CursorUtils.setWait(mainJBScrollPane);
                ApifoxTeam apifoxTeam = apifoxAPI.listTeam();
                ApifoxProject apifoxProject = apifoxAPI.listProject();

                DefaultMutableTreeNode root = new DefaultMutableTreeNode((""));
                for (ApifoxTeam.Team datum : apifoxTeam.getData()) {
                    List<ApifoxProject.Project> projects = apifoxProject.getData().stream().filter((s) -> s.getTeamId() == datum.getId()).collect(Collectors.toList());
                    TeamTreeNode teamTreeNode = new TeamTreeNode(datum, projects);
                    for (ApifoxProject.Project project : projects) {
                        ProjectTreeNode projectTreeNode = new ProjectTreeNode(datum, project);
                        projectTreeNodes.add(projectTreeNode);
                        teamTreeNode.add(projectTreeNode);
                    }
                    root.add(teamTreeNode);
                }
                TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
                selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                jTree.setSelectionModel(selectionModel);

                jTree.addTreeExpansionListener(ProjectDialog.this);
                ((DefaultTreeModel) jTree.getModel()).setRoot(root);
            } finally {
                CursorUtils.setDefault(mainJBScrollPane);
            }
        }).start();
        return mainJBScrollPane;
    }
}
