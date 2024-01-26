package com.cool.request.common.constant;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.component.http.StaticResourceServerService;
import com.cool.request.component.http.net.RequestContextManager;
import com.cool.request.utils.UserProjectManager;
import com.cool.request.view.IRequestParamManager;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.main.RequestEnvironmentProvide;
import com.cool.request.view.tool.CoolRequest;
import com.cool.request.view.tool.ToolActionPageSwitcher;
import com.intellij.openapi.util.Key;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface CoolRequestConfigConstant {
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
    com.intellij.openapi.util.Key<RequestEnvironmentProvide> RequestEnvironmentProvideKey = new Key<>(RequestEnvironmentProvide.class.getName());
    com.intellij.openapi.util.Key<ComponentCacheManager> ComponentCacheManagerKey = new Key<>(ComponentCacheManager.class.getName());
    com.intellij.openapi.util.Key<CoolRequest> CoolRequestKey = new Key<>(CoolRequest.class.getName());
    com.intellij.openapi.util.Key<Supplier<Boolean>> ServerMessageRefreshModelSupplierKey = new Key<>("ServerMessageRefreshModelSupplierKey");
    com.intellij.openapi.util.Key<MainTopTreeView> MainTopTreeViewKey = new Key<>(MainTopTreeView.class.getName());
    com.intellij.openapi.util.Key<Supplier<List<Controller>>> ControllerProvideKey = new Key<>("ControllerProvides");
    com.intellij.openapi.util.Key<ToolActionPageSwitcher> ToolActionPageSwitcherKey = new Key<>(ToolActionPageSwitcher.class.getName());
    com.intellij.openapi.util.Key<Map<Class<?>, Object>> ProviderMapKey = new Key<>("ProviderMapKey");
    com.intellij.openapi.util.Key<IRequestParamManager> IRequestParamManagerKey = new Key<>(IRequestParamManager.class.getName());
    com.intellij.openapi.util.Key<StaticResourceServerService> StaticResourceServerServiceKey = new Key<>(StaticResourceServerService.class.getName());

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
