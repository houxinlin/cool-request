/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ApifoxProjectFolderSelectDialog.java is part of Cool Request
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

package com.cool.request.plugin.apifox;

import com.cool.request.utils.CursorUtils;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
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
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ApifoxProjectFolderSelectDialog extends DialogWrapper implements TreeExpansionListener {
    private final SimpleTree jTree = new SimpleTree();
    private final List<ProjectTreeNode> projectTreeNodes = new ArrayList<>();
    private final List<FolderTreeNode> folderTreeNodes = new ArrayList<>();
    private final Map<TeamTreeNode, Boolean> folderGetStateCache = new HashMap<>();
    private final ApifoxAPI apifoxAPI;
    private final Project project;

    public ApifoxProjectFolderSelectDialog(Project project, ApifoxAPI apifoxAPI) {
        super(project);
        this.project = project;
        this.apifoxAPI = apifoxAPI;
        setTitle("Select Folder");
        DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        model.setRoot(new DefaultMutableTreeNode());
        jTree.setCellRenderer(new ApifoxColoredTreeCellRenderer());
        jTree.setRootVisible(false);
        jTree.setShowsRootHandles(true);
        TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree.setSelectionModel(selectionModel);
        ((SimpleTree) jTree).setPopupGroup(getPopupActions(), "ApifoxProjectFolderSelectDialog");
        init();
    }

    protected ActionGroup getPopupActions() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new CreateNewFolderAction(apifoxAPI, ((SimpleTree) jTree),
                (folderTreeNode, folder) -> {
                    folderTreeNode.add(new FolderTreeNode(folder));
                    jTree.updateUI();
                }));
        group.addSeparator();
        return group;
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
        if (lastPathComponent instanceof ApifoxProjectFolderSelectDialog.TeamTreeNode) {
            if (!folderGetStateCache.getOrDefault(lastPathComponent, false)) {
                CursorUtils.setWait(jTree);
                for (ApifoxProject.Project project : ((TeamTreeNode) lastPathComponent).getProject()) {
                    ApifoxFolder apifoxFolder = apifoxAPI.listFolder(project.getId());
                    CursorUtils.setDefault(jTree);
                    folderGetStateCache.put(((TeamTreeNode) lastPathComponent), true);
                    ProjectTreeNode projectTreeNode = getProjectTreeNodeById(project.getId());//項目
                    if (projectTreeNode != null) {
                        loadFolder(apifoxFolder, projectTreeNode);
                    }
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
                if (treeNode != null) {
                    treeNode.add(folderTreeNode);
                }
            }
        }
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
    }

    private FolderTreeNode getFolderTreeNodeById(int folderId) {
        for (FolderTreeNode folderTreeNode : folderTreeNodes) {
            if (folderTreeNode.getFolder().getId() == folderId) {
                return folderTreeNode;
            }
        }
        return null;
    }

    private ProjectTreeNode getProjectTreeNodeById(int projectId) {
        for (ProjectTreeNode projectTreeNode : projectTreeNodes) {
            if (projectTreeNode.getProject().getId() == projectId) {
                return projectTreeNode;
            }
        }
        return null;
    }

    public static void showDialog(Project project, ApifoxAPI apifoxAPI, Consumer<ApifoxFolder.Folder> consumer) {
        ApifoxProjectFolderSelectDialog apifoxProjectFolderSelectDialog = new ApifoxProjectFolderSelectDialog(project, apifoxAPI);
        apifoxProjectFolderSelectDialog.show();
        if (apifoxProjectFolderSelectDialog.isOK()) {
            consumer.accept(apifoxProjectFolderSelectDialog.getResult());
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

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TeamTreeNode that = (TeamTreeNode) o;
            return Objects.equals(team, that.team) && Objects.equals(project, that.project);
        }

        @Override
        public int hashCode() {
            return Objects.hash(team, project);
        }
    }

    private void loadFolders(Component component) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    CursorUtils.setWait(component);
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
                    ((DefaultTreeModel) jTree.getModel()).setRoot(root);
                } finally {
                    CursorUtils.setDefault(component);
                }
            }
        });

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JBScrollPane mainJBScrollPane = new JBScrollPane();
        mainJBScrollPane.setViewportView(jTree);
        setSize(400, 400);
        jTree.addTreeExpansionListener(ApifoxProjectFolderSelectDialog.this);
        loadFolders(jTree);
        return mainJBScrollPane;
    }
}
