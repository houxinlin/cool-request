package com.cool.request.lib.springmvc;

import com.cool.request.components.http.net.MediaTypes;

public class ByteBody implements Body {
    private byte[] bytes;

    public ByteBody(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte[] contentConversion() {
        return bytes != null ? bytes : new byte[0];
    }

    @Override
    public String getMediaType() {
        return MediaTypes.APPLICATION_STREAM;
    }
}
