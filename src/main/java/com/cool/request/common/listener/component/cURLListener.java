package com.cool.request.common.listener.component;

import com.cool.request.utils.ClipboardUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.view.IRequestParamManager;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.project.Project;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

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
        String newContent = ClipboardUtils.getClipboardText();
        if (newContent != null && (!newContent.equals(lastContent))) {
            if (StringUtils.isStartWithIgnoreSpace(newContent, "curl")) {
                ProviderManager.findAndConsumerProvider(IRequestParamManager.class, project, new Consumer<IRequestParamManager>() {
                    @Override
                    public void accept(IRequestParamManager iRequestParamManager) {
                        if (!iRequestParamManager.isAvailable()) return;
                    }
                });
            }
        }
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        super.windowLostFocus(e);
        lastContent = ClipboardUtils.getClipboardText();
    }
}
