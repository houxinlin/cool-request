package com.cool.request.action.nav;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.service.ControllerMapService;
import com.cool.request.utils.HttpMethodIconUtils;
import com.cool.request.utils.NavigationUtils;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.tool.ProviderManager;
import com.cool.request.view.tool.ToolActionPageSwitcher;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.List;

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
            //用户点击接口中的方法，接口中的方法有很多实现，所以这里要弹窗
            List<Controller> controllerByPsiMethod = ControllerMapService.getInstance(project).findControllerByPsiMethod(project, method);
            if (controllerByPsiMethod.size() > 1) {
                DefaultActionGroup defaultActionGroup = new DefaultActionGroup();

                for (Controller controller : controllerByPsiMethod) {
                    defaultActionGroup.add(new PsiMethodAnAction(controller));
                }
                DataContext dataContext = DataManager.getInstance().getDataContext(e.getComponent());
                JBPopupFactory.getInstance().createActionGroupPopup("Select a Url", defaultActionGroup, dataContext, JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        false).showInBestPositionFor(dataContext);
                return;
            }
            NavigationUtils.jumpToNavigation(project, method);
            ProviderManager.findAndConsumerProvider(ToolActionPageSwitcher.class, project, toolActionPageSwitcher -> {
                toolActionPageSwitcher.goToByName(MainBottomHTTPContainer.PAGE_NAME, null);
            });
            ProviderManager.findAndConsumerProvider(MainBottomHTTPContainer.class, project, mainBottomHTTPContainer -> {
                MainTopTreeView mainTopTreeView = ProviderManager.getProvider(MainTopTreeView.class, project);
                if (mainTopTreeView != null) {
                    mainBottomHTTPContainer.setAttachData(mainTopTreeView.getCurrentTreeNode());
                }

            });
        }
        // 双击发起请求
//        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
//            AtomicReference<TreePath> selectedPathIfOne = new AtomicReference<>();
//            ProviderManager.findAndConsumerProvider(MainTopTreeView.class, project, mainTopTreeView -> {
//                Tree tree = mainTopTreeView.getTree();
//                selectedPathIfOne.set(TreeUtil.getSelectedPathIfOne(tree));
//            });
//            ProviderManager.findAndConsumerProvider(ToolActionPageSwitcher.class, project, toolActionPageSwitcher -> {
//                toolActionPageSwitcher.goToByName(MainBottomHTTPContainer.PAGE_NAME,
//                        selectedPathIfOne.get().getLastPathComponent());
//            });
//        }
    }

    static class PsiMethodAnAction extends AnAction {
        private final Controller controller;

        public PsiMethodAnAction(Controller controller) {
            super(controller::getUrl, HttpMethodIconUtils.getIconByHttpMethod(controller.getHttpMethod()));
            this.controller = controller;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            NavigationUtils.jumpToControllerMethod(e.getProject(), controller);
        }
    }

}
