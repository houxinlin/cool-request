package com.hxl.plugin.springbootschedulerinvoke;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constant {
    public static final String LIB_NAME = "scheduled-invoke-starter.jar";
    public static final String CLASSPATH_LIB_PATH = "/lib/" + LIB_NAME;

    //工作目录
    public static final Path CONFIG_WORK_HOME = Paths.get(System.getProperty("user.home"), ".config", "hxl-plugin", "invoke");
    //lib目录
    public static final Path CONFIG_LIB_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "lib", LIB_NAME);
    //文件所目录
    public static final Path CONFIG_LOCK_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "port.lock");
    public static final Path CONFIG_LOG_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "invoke.log");
}
