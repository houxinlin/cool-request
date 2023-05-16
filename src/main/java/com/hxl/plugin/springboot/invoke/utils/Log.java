package com.hxl.plugin.springboot.invoke.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log {
    private static  PrintWriter LOG;

    static {
        try {
            LOG = new PrintWriter(new FileWriter("/home/LinuxWork/test/test.s"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void log(Object o){
        LOG.println(o.toString());
    }
}
