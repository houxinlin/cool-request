package com.cool.request.view.editor;

import com.cool.request.common.bean.components.controller.Controller;
import com.intellij.testFramework.LightVirtualFile;

public class CoolHTTPRequestVirtualFile extends LightVirtualFile {
    private final Controller controller;

    public CoolHTTPRequestVirtualFile(Controller controller) {
        super(controller.getUrl(), new CoolRequestFileType(controller), "");
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }
}
