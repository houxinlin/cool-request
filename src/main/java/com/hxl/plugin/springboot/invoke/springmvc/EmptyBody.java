package com.hxl.plugin.springboot.invoke.springmvc;

public class EmptyBody  implements Body{
    @Override
    public byte[] contentConversion() {
        return new byte[0];
    }
}
