package com.hxl.plugin.springboot.invoke;

import com.hxl.plugin.springboot.invoke.bean.components.controller.Controller;
import com.hxl.plugin.springboot.invoke.cache.ComponentCacheManager;
import com.hxl.plugin.springboot.invoke.net.RequestContextManager;
import com.hxl.plugin.springboot.invoke.tool.CoolRequest;
import com.hxl.plugin.springboot.invoke.tool.ToolActionPageSwitcher;
import com.hxl.plugin.springboot.invoke.utils.UserProjectManager;
import com.hxl.plugin.springboot.invoke.view.main.MainTopTreeView;
import com.hxl.plugin.springboot.invoke.view.main.MainViewDataProvide;
import com.intellij.openapi.util.Key;
import org.jetbrains.deft.Obj;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface Constant {
    String VERSION = "2024.2.1";
    String PLUGIN_ID = "Cool Request";
    String LIB_NAME = "spring-invoke-starter.jar";
    String SO_LIB_NAME = "dialog-utils.dll";
    String CLASSPATH_LIB_PATH = "/lib/" + LIB_NAME;
    String CLASSPATH_JAVAC_LIB_NAME = "/lib/javac.jar";
    String CLASSPATH_WINDOW_SO_LIB_PATH = "/lib/windows/" + SO_LIB_NAME;
    Path CONFIG_WORK_HOME = Paths.get(System.getProperty("user.home"), ".config", "spring-invoke", "invoke");
    Path CONFIG_LIB_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "lib", LIB_NAME);
    Path CONFIG_SO_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "lib", SO_LIB_NAME);
    Path CONFIG_JAVAC_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "lib", "javac.jar");
    Path CONFIG_CONTROLLER_SETTING = Paths.get(CONFIG_WORK_HOME.toString(), "controller-setting");
    Path CONFIG_RESPONSE_CACHE = Paths.get(CONFIG_WORK_HOME.toString(), "response-cache");
    Path CONFIG_DATA_CACHE = Paths.get(CONFIG_WORK_HOME.toString(), "data-cache");
    com.intellij.openapi.util.Key<UserProjectManager> UserProjectManagerKey = new Key<>(UserProjectManager.class.getName());
    com.intellij.openapi.util.Key<RequestContextManager> RequestContextManagerKey = new Key<>(RequestContextManager.class.getName());
    com.intellij.openapi.util.Key<Integer> PortKey = new Key<>("Listener_Port");
    com.intellij.openapi.util.Key<MainViewDataProvide> MainViewDataProvideKey = new Key<>(MainViewDataProvide.class.getName());
    com.intellij.openapi.util.Key<ComponentCacheManager> ComponentCacheManagerKey = new Key<>(ComponentCacheManager.class.getName());
    com.intellij.openapi.util.Key<CoolRequest> CoolRequestKey = new Key<>(CoolRequest.class.getName());
    com.intellij.openapi.util.Key<Supplier<Boolean>> ServerMessageRefreshModelSupplierKey = new Key<>("ServerMessageRefreshModelSupplierKey");
    com.intellij.openapi.util.Key<MainTopTreeView> MainTopTreeViewKey = new Key<>(MainTopTreeView.class.getName());
    com.intellij.openapi.util.Key<Supplier<List<Controller>>> ControllerProvideKey = new Key<>("ControllerProvides");
    com.intellij.openapi.util.Key<ToolActionPageSwitcher> ToolActionPageSwitcherKey = new Key<>(ToolActionPageSwitcher.class.getName());
    com.intellij.openapi.util.Key<Map<Class<?>, Object>> ProviderMapKey = new Key<>("ProviderMapKey");

    interface Identifier {
        String FILE = "file";
        String TEXT = "text";
    }

    interface URL {
        String PULL_ACTION = "https://plugin.houxinlin.com/api/action";
    }

    interface Colors {
        Color TABLE_SELECT_BACKGROUND = Color.decode("#64686a");
    }
}
