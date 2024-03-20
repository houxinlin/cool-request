package com.cool.request.view.tool;

import com.cool.request.common.bean.DynamicAnActionResponse;
import com.cool.request.common.bean.components.Component;
import com.cool.request.common.cache.ComponentCacheManager;
import com.cool.request.common.config.Version;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.components.ComponentType;
import com.cool.request.components.http.net.CommonOkHttpRequest;
import com.cool.request.components.http.net.RequestContextManager;
import com.cool.request.rmi.plugin.CoolRequestPluginRMIImpl;
import com.cool.request.rmi.plugin.ICoolRequestPluginRMI;
import com.cool.request.utils.GsonUtils;
import com.cool.request.utils.SocketUtils;
import com.cool.request.view.ViewRegister;
import com.cool.request.view.component.CoolRequestView;
import com.cool.request.view.main.RequestEnvironmentProvide;
import com.cool.request.view.tool.provider.RequestEnvironmentProvideImpl;
import com.intellij.openapi.project.Project;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CoolRequest implements Provider {
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private final UserProjectManager userProjectManager;
    private final ComponentCacheManager componentCacheManager;
    private CoolRequestView coolRequestView;
    private final int pluginListenerPort;
    private final Project project;

    /**
     * 项目启动后，但是窗口没打开，然后在打开窗口，将挤压的东西推送到窗口
     */
    private final Map<ComponentType, List<Component>> backlogData = new HashMap<>();

    public static synchronized CoolRequest initCoolRequest(Project project) {
        if (project.getUserData(CoolRequestConfigConstant.CoolRequestKey) != null)
            return project.getUserData(CoolRequestConfigConstant.CoolRequestKey);
        return new CoolRequest(project);
    }

    public void addBacklogData(ComponentType componentType, List<? extends Component> components) {
        backlogData.computeIfAbsent(componentType, componentType1 -> new ArrayList<>()).addAll(components);

    }

    private CoolRequest(Project project) {
        this.project = project;
        userProjectManager = new UserProjectManager(project, this);
        componentCacheManager = new ComponentCacheManager(project);
        initPluginRMIServer(project);
        // 拉取检查更新
        scheduledThreadPoolExecutor.scheduleAtFixedRate(this::pullNewAction, 0, 12, TimeUnit.HOURS);
        pluginListenerPort = SocketUtils.getSocketUtils().getPort(project);
    }

    /**
     * 拉取检查更新
     */
    private void pullNewAction() {
        CommonOkHttpRequest commonOkHttpRequest = new CommonOkHttpRequest();
        commonOkHttpRequest.getBody(CoolRequestConfigConstant.URL.PULL_ACTION).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
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

    /**
     * @param project 项目
     */
    private void initPluginRMIServer(Project project) {
        int port = SocketUtils.getSocketUtils().getPort(project);
        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(port);
            ICoolRequestPluginRMI iCoolRequestPluginRMI = new CoolRequestPluginRMIImpl(userProjectManager);
            registry.bind("@CoolRequestPluginRMI", iCoolRequestPluginRMI);
//            Disposer.register(CoolRequestPluginDisposable.getInstance(project), coolPluginSocketServer);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
//        CoolPluginSocketServer coolPluginSocketServer = CoolPluginSocketServer.newPluginSocketServer(new MessageHandlers(userProjectManager), port);
    }

    public synchronized void attachWindowView(CoolRequestView coolRequestView) {
        this.coolRequestView = coolRequestView;
        if (coolRequestView != null) {
            this.backlogData.forEach(userProjectManager::addComponent);
            backlogData.clear();
        }
    }

    public boolean canAddComponentToView() {
        return coolRequestView != null;
    }

    /**
     * 只有在窗口打开后数据提供器才被安装
     */
    public void installProviders() {
        ProviderManager.registerProvider(CoolRequest.class, CoolRequestConfigConstant.CoolRequestKey, this, project);
        ProviderManager.registerProvider(RequestEnvironmentProvide.class, CoolRequestConfigConstant.RequestEnvironmentProvideKey,
                new RequestEnvironmentProvideImpl(project), project);
        ProviderManager.registerProvider(ViewRegister.class, CoolRequestConfigConstant.ViewRegisterKey, new ViewRegister(), project);
        ProviderManager.registerProvider(UserProjectManager.class, CoolRequestConfigConstant.UserProjectManagerKey, userProjectManager, project);

        project.putUserData(CoolRequestConfigConstant.RequestContextManagerKey, new RequestContextManager());


    }

}
