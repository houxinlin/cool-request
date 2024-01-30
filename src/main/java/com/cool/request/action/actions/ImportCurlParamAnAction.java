package com.cool.request.action.actions;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.utils.CURLUtils;
import com.cool.request.utils.ClipboardUtils;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.view.dialog.BigInputDialog;
import com.cool.request.view.main.IRequestParamManager;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.LayeredIcon;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ImportCurlParamAnAction extends BaseAnAction {
    public static final Icon ADD_WITH_DROPDOWN = new LayeredIcon(CoolRequestIcons.CURL, AllIcons.General.Dropdown);

    public ImportCurlParamAnAction(Project project) {
        super(project, () -> "curl", ADD_WITH_DROPDOWN);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();

        DefaultActionGroup defaultActionGroup = new DefaultActionGroup(new ImportCurlAnAction(project),
                new CopyCurrentNodeAsCurl(project));
        defaultActionGroup.getTemplatePresentation().setIcon(ADD_WITH_DROPDOWN);
        defaultActionGroup.getTemplatePresentation().setText("cURL");
        defaultActionGroup.registerCustomShortcutSet(CommonShortcuts.getNewForDialogs(), null);

        JBPopupFactory.getInstance().createActionGroupPopup(
                        null, defaultActionGroup, e.getDataContext(), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        false, null, 10, null, "popup@ImportCurlParamAnAction")
                .showUnderneathOf(e.getInputEvent().getComponent());
    }

    public static class CopyCurrentNodeAsCurl extends BaseAnAction {
        public CopyCurrentNodeAsCurl(Project project) {
            super(project, () -> "Copy Curl", CoolRequestIcons.COPY);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            ProviderManager.findAndConsumerProvider(IRequestParamManager.class, getProject(), iRequestParamManager -> {
                if (iRequestParamManager.isAvailable()) {
                    ClipboardUtils.copyToClipboard(CURLUtils.generatorCurl(getProject(), iRequestParamManager.getCurrentController()));
                }
            });
        }
    }

    public static class ImportCurlAnAction extends BaseAnAction {
        public ImportCurlAnAction(Project project) {
            super(project, () -> "Import", CoolRequestIcons.CURL);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            try {

                BigInputDialog bigInputDialog = new BigInputDialog(getProject(), ResourceBundleUtils.getString("import.curl.tip"));
                bigInputDialog.show();
                //找到参数管理器，设置header、formdata、json参数
                ProviderManager.findAndConsumerProvider(IRequestParamManager.class, getProject(), iRequestParamManager -> {
                    if (iRequestParamManager.isAvailable()) {
                        iRequestParamManager.importCurl(bigInputDialog.getValue());
                    } else {
                        MessagesWrapperUtils.showOkCancelDialog("Please Select API", "Tip", CoolRequestIcons.MAIN);
                    }
                });
            } catch (IllegalArgumentException exception) {
                Messages.showErrorDialog("Unable to parse parameters", "Tip");
            }
        }
    }


}
