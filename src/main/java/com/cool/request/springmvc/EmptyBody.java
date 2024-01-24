package com.cool.request.springmvc;

public class EmptyBody  implements Body{
    @Override
    public byte[] contentConversion() {
        return new byte[0];
    }
}
