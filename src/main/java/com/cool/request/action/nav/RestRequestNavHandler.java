package com.cool.request.action.nav;

import com.cool.request.utils.NavigationUtils;
import com.cool.request.view.component.ApiToolPage;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.tool.ProviderManager;
import com.cool.request.view.tool.ToolActionPageSwitcher;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhangpj
 * @date 2024/01/17
 */
public class RestRequestNavHandler implements GutterIconNavigationHandler<PsiElement> {

    @Override
    public void navigate(MouseEvent e, PsiElement elt) {
        Project project = elt.getProject();
        PsiMethod method = (PsiMethod) elt.getParent();
        // 单击导航
        if (SwingUtilities.isLeftMouseButton(e)) {
            NavigationUtils.jumpToNavigation(project, method);
            AtomicReference<TreePath> selectedPathIfOne = new AtomicReference<>();
            ProviderManager.findAndConsumerProvider(MainTopTreeView.class, project, mainTopTreeView -> {
                Tree tree = mainTopTreeView.getTree();
                selectedPathIfOne.set(TreeUtil.getSelectedPathIfOne(tree));
            });
            ProviderManager.findAndConsumerProvider(ToolActionPageSwitcher.class, project, toolActionPageSwitcher -> {
                toolActionPageSwitcher.goToByName(ApiToolPage.PAGE_NAME,
                        selectedPathIfOne.get().getLastPathComponent());
            });
        }
        // 双击发起请求
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            AtomicReference<TreePath> selectedPathIfOne = new AtomicReference<>();
            ProviderManager.findAndConsumerProvider(MainTopTreeView.class, project, mainTopTreeView -> {
                Tree tree = mainTopTreeView.getTree();
                selectedPathIfOne.set(TreeUtil.getSelectedPathIfOne(tree));
            });
            ProviderManager.findAndConsumerProvider(ToolActionPageSwitcher.class, project, toolActionPageSwitcher -> {
                toolActionPageSwitcher.goToByName(MainBottomHTTPContainer.PAGE_NAME,
                        selectedPathIfOne.get().getLastPathComponent());
            });
        }
    }

}
