package com.cool.request.action.actions;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.TreePathUtils;
import com.cool.request.view.editor.CoolRequestFileType;
import com.cool.request.view.main.MainTopTreeView;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
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

    private VirtualFile createNewFile(Project project) {
        Controller controller = getController();
        LightVirtualFile virtualFile = new LightVirtualFile(controller.getUrl(), "content");
        virtualFile.putUserData(CoolRequestConfigConstant.OpenHTTPREquestPageTabKey, controller);
        virtualFile.setFileType(new CoolRequestFileType(controller));
        return virtualFile;

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                VirtualFile virtualFile = createNewFile(project);
                FileEditorManager.getInstance(project).openFile(virtualFile, true);
            });
        }
    }
}
