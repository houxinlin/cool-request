package com.cool.request.common.service;

import com.cool.request.utils.ClipboardUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

@Service
public final class ClipboardService {
    private String curlData;

    public static final ClipboardService getInstance() {
        return ApplicationManager.getApplication().getService(ClipboardService.class);
    }

    public void copyCUrl(String curlData) {
        this.curlData = curlData;
        ClipboardUtils.copyToClipboard(curlData);
    }

    public void setCurlData(String curlData) {
        this.curlData = curlData;
    }

    public String getCurlData() {
        return curlData;
    }
}
