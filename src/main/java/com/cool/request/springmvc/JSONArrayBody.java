package com.cool.request.springmvc;

public class JSONArrayBody  implements Body{
    @Override
    public byte[] contentConversion() {
        return new byte[0];
    }
}
