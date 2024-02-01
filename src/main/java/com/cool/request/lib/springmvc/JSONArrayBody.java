package com.cool.request.lib.springmvc;

import com.cool.request.component.http.net.MediaTypes;

public class JSONArrayBody  implements Body{
    @Override
    public byte[] contentConversion() {
        return new byte[0];
    }

    @Override
    public String getMediaType() {
        return MediaTypes.APPLICATION_JSON;
    }
}
