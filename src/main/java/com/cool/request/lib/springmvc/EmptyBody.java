package com.cool.request.lib.springmvc;

public class EmptyBody  implements Body{
    @Override
    public byte[] contentConversion() {
        return new byte[0];
    }

    @Override
    public String getMediaType() {
        return null;
    }
}
