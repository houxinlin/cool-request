package com.hxl.plugin.springbootschedulerinvoke;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constant {
    public static final String LIB_NAME = "scheduled-invoke-starter.jar";
    public static final String CLASSPATH_LIB_PATH = "/lib/" + LIB_NAME;
    public static final Path CONFIG_DIR = Paths.get(System.getProperty("user.home"), ".config", "hxl-plugin", "lib");
    public static final Path CONFIG_LIB_PATH = Paths.get(CONFIG_DIR.toString(), LIB_NAME);

    public static final Path CONFIG_LOCK_PATH = Paths.get(System.getProperty("user.home"), ".config", "hxl-plugin", "port.lock");
}
