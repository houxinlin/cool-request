/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CoolRequest.java is part of Cool Request
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

package com.cool.request.view.tool;

import com.cool.request.common.bean.DynamicAnActionResponse;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.common.config.Version;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.components.http.net.CommonOkHttpRequest;
import com.cool.request.rmi.agent.ICoolRequestAgentServer;
import com.cool.request.rmi.agent.ICoolRequestAgentServerImpl;
import com.cool.request.rmi.plugin.CoolRequestPluginRMIImpl;
import com.cool.request.rmi.plugin.ICoolRequestPluginRMI;
import com.cool.request.utils.GsonUtils;
import com.cool.request.utils.SocketUtils;
import com.cool.request.view.component.CoolRequestView;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public final class CoolRequest {
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private CoolRequestView coolRequestView;
    private int pluginListenerPort;
    private Project project;
    private Registry rmiRegistry = null;
    private boolean init = false;

    public static CoolRequest getInstance(Project project) {
        return project.getService(CoolRequest.class);
    }

    public CoolRequest init() {
        ComponentCacheManager.getInstance(project).init();
        UserProjectManager.getInstance(project).init();
        initPluginRMIServer();
        // 拉取检查更新
        scheduledThreadPoolExecutor.scheduleAtFixedRate(this::pullNewAction, 0, 5, TimeUnit.HOURS);
        pluginListenerPort = SocketUtils.getSocketUtils().getPort(project);
        init = true;
        return this;
    }

    private CoolRequest(Project project) {
        this.project = project;
    }

    /**
     * 拉取检查更新
     */
    private void pullNewAction() {
        CommonOkHttpRequest commonOkHttpRequest = new CommonOkHttpRequest();
        commonOkHttpRequest.getBody(CoolRequestConfigConstant.URL.PULL_ACTION).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    if (response.code() == 200) {
                        String body = response.body().string();
                        DynamicAnActionResponse dynamicAnActionResponse = GsonUtils.readValue(body, DynamicAnActionResponse.class);

                        if (new Version(CoolRequestConfigConstant.VERSION).compareTo(new Version(dynamicAnActionResponse.getLastVersion())) < 0) {
                            if (coolRequestView != null) {
                                SwingUtilities.invokeLater(() -> coolRequestView.showUpdateMenu());
                            }
                        }
                        if (coolRequestView == null) return;
                        coolRequestView.removeAllDynamicAnActions();
                        for (DynamicAnActionResponse.AnAction action : dynamicAnActionResponse.getActions()) {
                            ImageIcon imageIcon = new ImageIcon(ImageIO.read(new URL(action.getIconUrl())));
                            coolRequestView.addNewDynamicAnAction(action.getName(), action.getValue(), imageIcon);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        });
    }

    public int getPluginListenerPort() {
        return pluginListenerPort;
    }

    private void initPluginRMIServer() {
        int port = SocketUtils.getSocketUtils().getPort(project);
        try {
            rmiRegistry = LocateRegistry.createRegistry(port);
            rmiRegistry.bind(ICoolRequestPluginRMI.class.getName(), new CoolRequestPluginRMIImpl(project));
            rmiRegistry.bind(ICoolRequestAgentServer.class.getName(), new ICoolRequestAgentServerImpl(project));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isInit() {
        return init;
    }

    public boolean canAddComponentToView() {
        return coolRequestView != null;
    }

    public void attachView(CoolRequestView coolRequestView) {
        this.coolRequestView = coolRequestView;
    }
}
