package com.cool.request.action.actions;

import com.cool.request.components.http.Controller;
import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.cool.request.common.service.ClipboardService;
import com.cool.request.utils.CURLUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.param.PanelParameterProvider;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.dialog.BigInputDialog;
import com.cool.request.view.main.IRequestParamManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;

public class CurlParamAnAction extends DynamicAnAction {

    private final MainBottomHTTPContainer mainBottomHTTPContainer;
    private final DefaultActionGroup defaultActionGroup;

    public CurlParamAnAction(Project project, MainBottomHTTPContainer mainBottomHTTPContainer) {
        super(project, () -> "cURL", () -> KotlinCoolRequestIcons.INSTANCE.getCURL().invoke());
        this.mainBottomHTTPContainer = mainBottomHTTPContainer;
        this.defaultActionGroup = new DefaultActionGroup(
                new ImportCurlAnAction(project),
                new CopyCurrentNodeAsCurl(project));
        defaultActionGroup.getTemplatePresentation().setText("cURL");

    }

    private IRequestParamManager getRequestParamManager() {
        return mainBottomHTTPContainer.getMainBottomHttpInvokeViewPanel().getHttpRequestParamPanel();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        defaultActionGroup.registerCustomShortcutSet(CommonShortcuts.getNewForDialogs(), null);

        JBPopupFactory.getInstance().createActionGroupPopup(
                        null, defaultActionGroup, e.getDataContext(), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        false, null, 10, null, "popup@ImportCurlParamAnAction")
                .showUnderneathOf(e.getInputEvent().getComponent());
    }

    private class CopyCurrentNodeAsCurl extends DynamicAnAction {
        public CopyCurrentNodeAsCurl(Project project) {
            super(project, () -> "Copy As Curl", KotlinCoolRequestIcons.INSTANCE.getCOPY());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Controller attachController = mainBottomHTTPContainer.getAttachController();
            if (attachController == null) return;

            String curl = CURLUtils.generatorCurl(getProject(), attachController,
                    new PanelParameterProvider(
                            mainBottomHTTPContainer.getMainBottomHttpInvokeViewPanel()
                                    .getHttpRequestParamPanel()));
            ClipboardService.getInstance().copyCUrl(curl);
        }
    }

    private class ImportCurlAnAction extends DynamicAnAction {
        public ImportCurlAnAction(Project project) {
            super(project, () -> "Import Curl", KotlinCoolRequestIcons.INSTANCE.getIMPORT());
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            try {
                BigInputDialog bigInputDialog = new BigInputDialog(getProject(), ResourceBundleUtils.getString("import.curl.tip"));
                bigInputDialog.show();
                //找到参数管理器，设置header、formdata、json参数
                mainBottomHTTPContainer.getMainBottomHttpInvokeViewPanel()
                        .getHttpRequestParamPanel().importCurl(bigInputDialog.getValue());
            } catch (IllegalArgumentException exception) {
                Messages.showErrorDialog("Unable to parse parameters", ResourceBundleUtils.getString("tip"));
            }
        }
    }


}
