package com.cool.request.action.actions;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.TreePathUtils;
import com.cool.request.view.editor.CoolHTTPRequestVirtualFile;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class OpenHTTPRequestPageTab extends BaseAnAction {
    private MainTopTreeView mainTopTreeView;

    public OpenHTTPRequestPageTab(Project project, MainTopTreeView mainTopTreeView) {
        super(project, () -> ResourceBundleUtils.getString("open.http.request.new.tab"), CoolRequestIcons.MAIN);
        this.mainTopTreeView = mainTopTreeView;
    }

    private Controller getController() {
        TreePath selectedPathIfOne = TreeUtil.getSelectedPathIfOne(mainTopTreeView.getTree());
        if (selectedPathIfOne != null) {
            MainTopTreeView.RequestMappingNode requestMappingNode = TreePathUtils.getNode(selectedPathIfOne, MainTopTreeView.RequestMappingNode.class);
            assert requestMappingNode != null;
            return requestMappingNode.getData();
        }
        return null;
    }

    private VirtualFile createNewFile() {
        Controller controller = getController();
        if (controller != null) {
            return new CoolHTTPRequestVirtualFile(controller);
        }
        return null;

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                VirtualFile virtualFile = createNewFile();
                if (virtualFile!=null){
                    FileEditorManager.getInstance(project).openFile(virtualFile, true);
                }
            });
        }
    }
}
