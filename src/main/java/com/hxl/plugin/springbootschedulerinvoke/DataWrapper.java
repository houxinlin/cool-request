package com.hxl.plugin.springbootschedulerinvoke;

import java.io.ByteArrayOutputStream;
import java.nio.channels.SocketChannel;

public class DataWrapper {
    public int size;
    public ByteArrayOutputStream data;
    public SocketChannel socketChannel;

    public DataWrapper(int size, ByteArrayOutputStream data, SocketChannel socketChannel) {
        this.size = size;
        this.data = data;
        this.socketChannel = socketChannel;
    }

    public void readFinish() {
        System.out.println(this.data.toString());
    }

    @Override
    public String toString() {
        return data.toString();
    }
}