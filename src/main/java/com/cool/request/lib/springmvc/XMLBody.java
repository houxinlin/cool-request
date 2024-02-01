package com.cool.request.lib.springmvc;

import com.cool.request.component.http.net.MediaTypes;

public class XMLBody extends StringBody {
    public XMLBody(String value) {
        super(value);
    }
    @Override
    public String getMediaType() {
        return MediaTypes.APPLICATION_XML;
    }
}
