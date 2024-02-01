package com.cool.request.lib.springmvc;

import com.cool.request.component.http.net.MediaTypes;

public class JSONBody  extends StringBody{
    public JSONBody(String value) {
        super(value);
    }

    @Override
    public String getMediaType() {
        return MediaTypes.APPLICATION_JSON;
    }
}
