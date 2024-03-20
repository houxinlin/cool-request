package com.cool.request.components.http;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.components.http.net.HTTPHeader;
import com.cool.request.components.http.net.MediaTypes;
import com.cool.request.utils.ResourceBundleUtils;

public class OverflowHttpResponseBodyConverter implements HTTPResponseBodyConverter {
    @Override
    public byte[] bodyConverter(byte[] httpResponseBody, HTTPHeader header) {
        int maxSize = SettingPersistentState.getInstance().getState().maxHTTPResponseSize * 1024 * 1024;
        if (httpResponseBody.length >maxSize) {
            header.setHeader(HTTPHeader.CONTENT_TYPE, MediaTypes.TEXT);
            return ResourceBundleUtils.getString("big.data.reject").getBytes();
        } else {
            return httpResponseBody;
        }
    }
}
