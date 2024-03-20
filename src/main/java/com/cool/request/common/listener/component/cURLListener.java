package com.cool.request.common.listener.component;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.service.ClipboardService;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.components.CoolRequestContext;
import com.cool.request.utils.ClipboardUtils;
import com.cool.request.utils.MessagesWrapperUtils;
import com.cool.request.utils.ResourceBundleUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.main.IRequestParamManager;
import com.intellij.openapi.project.Project;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * curl监听器
 */
public class cURLListener extends WindowAdapter {
    private Project project;
    private String lastContent;

    public cURLListener(Project project) {
        this.project = project;
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        super.windowGainedFocus(e);
        if (project.isDisposed()) return;
        if (!SettingPersistentState.getInstance().getState().listenerCURL) return;
        String newContent = ClipboardUtils.getClipboardText();
        if (newContent != null && (!newContent.equals(lastContent))) {
            if (StringUtils.isEqualsIgnoreCase(ClipboardService.getInstance().getCurlData(), newContent)) return;
            if (StringUtils.isStartWithIgnoreSpace(newContent, "curl")) {
                IRequestParamManager mainRequestParamManager = CoolRequestContext.getInstance(project).getMainRequestParamManager();
                MessagesWrapperUtils.showOkCancelDialog(ResourceBundleUtils.getString("import.curl.tip.auto"),
                        ResourceBundleUtils.getString("tip"), CoolRequestIcons.MAIN, integer -> {
                            if (0 == integer) mainRequestParamManager.importCurl(newContent);
                        });
            }
        }
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        super.windowLostFocus(e);
        if (project.isDisposed()) return;
        lastContent = ClipboardUtils.getClipboardText();
    }
}
