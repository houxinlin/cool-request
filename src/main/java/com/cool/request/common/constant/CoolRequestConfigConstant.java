/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CoolRequestConfigConstant.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.common.constant;

import com.cool.request.components.http.net.RequestContextManager;
import com.cool.request.view.component.MainBottomHTTPContainer;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.main.MainTopTreeViewManager;
import com.cool.request.view.tool.ToolActionPageSwitcher;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public interface CoolRequestConfigConstant {
    String VERSION = "2024.5.1";
    String PLUGIN_ID = "Cool Request";
    String LIB_NAME = "spring-invoke-starter.jar";
    String SCRIPT_NAME = "cool-request-script-api.jar";
    String CLASSPATH_LIB_PATH = "/lib/" + LIB_NAME;
    String COOL_REQUEST_AGENT = "/lib/cool-request-agent.jar";
    String CLASSPATH_SCRIPT_API_PATH = "/lib/" + SCRIPT_NAME;
    String CLASSPATH_JAVAC_LIB_NAME = "/lib/javac.jar";
    Path CONFIG_WORK_HOME = Paths.get(System.getProperty("user.home"), ".config", ".cool-request", "request");
    Path LOG_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "log");
    Path CONFIG_LIB_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "lib", LIB_NAME);
    Path CONFIG_SCRIPT_LIB_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "lib", SCRIPT_NAME);
    Path CONFIG_AGENT_LIB_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "lib", "cool-request-agent.jar");
    Path CONFIG_JAVAC_PATH = Paths.get(CONFIG_WORK_HOME.toString(), "lib", "javac.jar");
    Path CONFIG_CONTROLLER_SETTING = Paths.get(CONFIG_WORK_HOME.toString(), "controller-setting");
    Path CONFIG_RESPONSE_CACHE = Paths.get(CONFIG_WORK_HOME.toString(), "response-cache");
    Path CONFIG_DATA_CACHE = Paths.get(CONFIG_WORK_HOME.toString(), "data-cache");
    com.intellij.openapi.util.Key<RequestContextManager> RequestContextManagerKey = new Key<>(RequestContextManager.class.getName());
    com.intellij.openapi.util.Key<Integer> PortKey = new Key<>("Listener_Port");
    com.intellij.openapi.util.Key<MainTopTreeView> MainTopTreeViewKey = new Key<>(MainTopTreeView.class.getName());
    com.intellij.openapi.util.Key<MainTopTreeViewManager> MainTopTreeViewManagerKey = new Key<>(MainTopTreeViewManager.class.getName());
    com.intellij.openapi.util.Key<ToolActionPageSwitcher> ToolActionPageSwitcherKey = new Key<>(ToolActionPageSwitcher.class.getName());
    com.intellij.openapi.util.Key<Map<Class<?>, Object>> ProviderMapKey = new Key<>("ProviderMapKey");
    com.intellij.openapi.util.Key<MainBottomHTTPContainer> MainBottomHTTPContainerKey = new Key<>(MainBottomHTTPContainer.class.getName());
    com.intellij.openapi.util.Key<ToolWindow> ToolWindowKey = new Key<>("CoolToolWindow");

    interface Identifier {
        String FILE = "file";
        String TEXT = "text";
    }

    interface URL {
        String PULL_ACTION = "https://coolrequestapi.coolrequest.dev/api/action";
        String STATIC_SERVER_HELP = "https://coolrequest.dev/docs/tutorial-basics/static_server";
        String ERROR_REPORT = "https://coolrequestapi.coolrequest.dev/api/error/report";
    }

    interface Colors {
        Color TABLE_SELECT_BACKGROUND = new JBColor(Color.decode("#F2F2F2"), Color.decode("#64686a"));
        Color GREEN = Color.decode("#1A9F6C");
        Color RED = Color.decode("#E74C3C");
        Color BLUE = Color.decode("#0585D2");

        Color Summary = new JBColor(Color.decode("#A6A5A3"), Color.decode("#A6A5A3"));
    }
}
