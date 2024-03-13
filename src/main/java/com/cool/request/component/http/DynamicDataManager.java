package com.cool.request.component.http;

import com.cool.request.common.bean.MultipleMap;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.DynamicController;
import com.cool.request.common.model.ProjectStartupModel;
import com.cool.request.component.http.invoke.ReflexPullDynamicRequest;
import com.cool.request.component.http.invoke.body.PullDynamicRequestBody;
import com.cool.request.utils.UrlUtils;
import com.cool.request.view.tool.ProviderManager;
import com.cool.request.view.tool.UserProjectManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.net.MalformedURLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service()
public final class DynamicDataManager {
    private Project project;

    private final MultipleMap<String, CallbackWrapper, CountDownLatch> waitMap = new MultipleMap<>();

    public static DynamicDataManager getInstance(Project project) {
        DynamicDataManager service = project.getService(DynamicDataManager.class);
        service.setProject(project);
        return service;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void dataNotify(DynamicController dynamicController) {
        if (waitMap.containsKey(dynamicController.getId())) {
            SwingUtilities.invokeLater(() -> {
                waitMap.getFirstValue(dynamicController.getId()).successCallback.accept(dynamicController);
                waitMap.getSecondValue(dynamicController.getId()).countDown();
                waitMap.remove(dynamicController.getId());
            });
        }
    }

    public void pullDynamicData(String url, Controller controller, Consumer<DynamicController> successCallback, Runnable failCallback) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        if (waitMap.containsKey(controller.getId())) return;
        waitMap.put(controller.getId(), new CallbackWrapper(successCallback, failCallback), countDownLatch);
        new Thread(new PullDynamicDataThread(controller, countDownLatch, url)).start();
    }


    class PullDynamicDataThread implements Runnable {
        private final Controller controller;
        private final CountDownLatch countDownLatch;
        private final String url;

        public PullDynamicDataThread(Controller controller, CountDownLatch countDownLatch, String url) {
            this.controller = controller;
            this.url = url;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            UserProjectManager userProjectManager = ProviderManager.getProvider(UserProjectManager.class, project);
            if (userProjectManager == null) return;
            try {
                int port = UrlUtils.getPort(url);
                for (ProjectStartupModel projectStartupModel : userProjectManager.getSpringBootApplicationStartupModel()) {
                    if (projectStartupModel.getProjectPort() == port) {
                        PullDynamicRequestBody pullDynamicRequestBody = new PullDynamicRequestBody();
                        pullDynamicRequestBody.setClassName(controller.getJavaClassName());
                        new ReflexPullDynamicRequest(projectStartupModel.getPort()).request(pullDynamicRequestBody);
                    }
                }
                try {
                    if (!countDownLatch.await(3, TimeUnit.SECONDS)) {
                        if (waitMap.containsKey(controller.getId())) {
                            waitMap.getFirstValue(controller.getId()).failCallback.run();
                            waitMap.remove(controller.getId());
                        }
                    }
                } catch (InterruptedException ignored) {

                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }


        }
    }

    static class CallbackWrapper {
        private final Consumer<DynamicController> successCallback;
        private final Runnable failCallback;

        public CallbackWrapper(Consumer<DynamicController> successCallback, Runnable failCallback) {
            this.successCallback = successCallback;
            this.failCallback = failCallback;
        }
    }

}
