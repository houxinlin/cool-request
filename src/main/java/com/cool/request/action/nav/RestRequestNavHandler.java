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
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.List;

import static com.cool.request.common.constant.CoolRequestConfigConstant.PLUGIN_ID;

/**
 * @author zhangpj
 * @date 2024/01/17
 */
public class RestRequestNavHandler implements GutterIconNavigationHandler<PsiElement> {

    @Override
    public void navigate(MouseEvent e, PsiElement elt) {
        Project project = elt.getProject();
        PsiMethod method = (PsiMethod) elt.getParent();

        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PLUGIN_ID);
        if (toolWindow != null) {
            toolWindow.show();
        }
        // 单击导航
        if (SwingUtilities.isLeftMouseButton(e)) {
            //用户点击接口中的方法，接口中的方法有很多实现，所以这里要弹窗
            ControllerMapService controllerMapService = ControllerMapService.getInstance(project);
            List<Controller> controllerByPsiMethod = controllerMapService.findControllerByPsiMethod(project, method);
            if (controllerByPsiMethod.size() > 1) {
                DefaultActionGroup defaultActionGroup = new DefaultActionGroup();

                for (Controller controller : controllerByPsiMethod) {
                    defaultActionGroup.add(new PsiMethodAnAction(controller));
                }
                DataContext dataContext = DataManager.getInstance().getDataContext(e.getComponent());
                JBPopupFactory.getInstance().createActionGroupPopup("Choose a URL", defaultActionGroup, dataContext, JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        false).show(new RelativePoint(e.getLocationOnScreen()));
                return;
            }
            if (controllerByPsiMethod.isEmpty()) {
                return;
            }

            //HTTP请求界面选中
            ProviderManager.findAndConsumerProvider(ToolActionPageSwitcher.class, project, toolActionPageSwitcher -> {
                toolActionPageSwitcher.goToByName(MainBottomHTTPContainer.PAGE_NAME, null);
            });
            if (!controllerByPsiMethod.isEmpty()) {
                MainTopTreeView.RequestMappingNode requestMappingNodeByController = controllerMapService.findRequestMappingNodeByController(project, controllerByPsiMethod.get(0));
                if (requestMappingNodeByController == null) return;
                //JTree中选择节点
                ProviderManager.findAndConsumerProvider(MainTopTreeView.class, project, mainTopTreeView -> {
                    mainTopTreeView.selectNode(requestMappingNodeByController);
                });
                //HTTP请求界面选中JTree的节点
                ProviderManager.findAndConsumerProvider(MainBottomHTTPContainer.class, project, mainBottomHTTPContainer -> {
                    MainTopTreeView mainTopTreeView = ProviderManager.getProvider(MainTopTreeView.class, project);
                    if (mainTopTreeView != null) {
                        mainBottomHTTPContainer.setAttachData(mainTopTreeView.getCurrentTreeNode());
                    }
                });
            }

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
