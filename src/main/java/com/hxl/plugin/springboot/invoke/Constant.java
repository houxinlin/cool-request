package com.hxl.plugin.springboot.invoke;

import com.hxl.plugin.springboot.invoke.utils.UserProjectManager;
import com.intellij.openapi.util.Key;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface Constant {
    public static final String PLUGIN_ID="Cool Request";
    public static final String LIB_NAME = "spring-invoke-starter.jar";
    public static final String SO_LIB_NAME = "dialog-utils.dll";
    public static final String CLASSPATH_LIB_PATH = "/lib/" + LIB_NAME;
    public static final String CLASSPATH_JAVAC_LIB_NAME = "/lib/javac.jar";
    public static final String CLASSPATH_WINDOW_SO_LIB_PATH = "/lib/windows/" + SO_LIB_NAME;
    public static final Path CONFIG_WORK_HOME = Paths.get(System.getProperty("user.home"), ".config", "spring-invoke", "invoke");
    public static final Path CONFIG_LIB_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "lib", LIB_NAME);
    public static final Path CONFIG_SO_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "lib", SO_LIB_NAME);
    public static final Path CONFIG_JAVAC_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "lib", "javac.jar");
    public static final Path CONFIG_CONTROLLER_SETTING = Paths.get(CONFIG_WORK_HOME.toString(), "controller-setting");
    public static final Path CONFIG_RESPONSE_CACHE = Paths.get(CONFIG_WORK_HOME.toString(), "response-cache");
    public static final com.intellij.openapi.util.Key<UserProjectManager> UserProjectManagerKey = new Key<>(UserProjectManager.class.getName());
    public static final com.intellij.openapi.util.Key<Integer> PortKey = new Key<>("Listener_Port");

    public interface Identifier {
        public static final String FILE = "file";
        public static final String TEXT = "text";
    }
}
