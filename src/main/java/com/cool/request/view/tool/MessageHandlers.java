package com.cool.request.view.tool;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.model.*;
import com.cool.request.common.state.CoolRequestEnvironmentPersistentComponent;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.cool.request.component.http.net.RequestManager;
import com.cool.request.utils.ComponentIdUtils;
import com.cool.request.utils.ObjectMappingUtils;
import com.cool.request.utils.PsiUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class MessageHandlers {
    private final UserProjectManager userProjectManager;
    private final Map<String, ServerMessageHandler> messageHandlerMap = new HashMap<>();

    private boolean getServerMessageRefreshModelValue() {
        Supplier<Boolean> supplier = userProjectManager.getProject().getUserData(CoolRequestConfigConstant.ServerMessageRefreshModelSupplierKey);
        if (supplier != null) return Optional.ofNullable(supplier.get()).orElse(Boolean.TRUE);
        return true;
    }

    public MessageHandlers(UserProjectManager userProjectManager) {
        this.userProjectManager = userProjectManager;
        Function<String, Boolean> filter = msgType -> {
            SettingsState state = SettingPersistentState.getInstance().getState();
            if ("controller".equals(msgType) || "scheduled".equals(msgType)) {
                //如果自动刷新处于关闭状态,并且没有提供手动刷新开关
                return !state.autoRefreshData && !getServerMessageRefreshModelValue();
            }
            if ("spring_gateway".equals(msgType)) {
                return !state.listenerGateway;
            }
            return false;
        };
        putNewMessageHandler("controller", new FilterServerMessageHandler(new ControllerInfoServerMessageHandler(), filter));
        putNewMessageHandler("response_info", new ResponseInfoServerMessageHandler());
        putNewMessageHandler("clear", new ClearServerMessageHandler());
        putNewMessageHandler("scheduled", new FilterServerMessageHandler(new ScheduledServerMessageHandler(), filter));
        putNewMessageHandler("startup", new ProjectStartupServerMessageHandler());
        putNewMessageHandler("invoke_receive", new RequestReceiveMessageHandler());
        putNewMessageHandler("spring_gateway", new FilterServerMessageHandler(new SpringGatewayMessageHandler(), filter));

    }

    private void putNewMessageHandler(String type, ServerMessageHandler serverMessageHandler) {
        messageHandlerMap.put(type, serverMessageHandler);
        if (serverMessageHandler instanceof BaseServerMessageHandler) {
            ((BaseServerMessageHandler) serverMessageHandler).setMsgType(type);
        }
    }

    /**
     * 服务端数据分发
     *
     * @param msg 服务端json数据
     */
    public void handlerMessage(String msg) {
        try {
            System.out.println(msg);
            MessageType messageType = ObjectMappingUtils.readValue(msg, MessageType.class);
            if (!StringUtils.isEmpty(messageType)) {
                if (messageHandlerMap.containsKey(messageType.getType())) {
                    messageHandlerMap.get(messageType.getType()).handler(msg);
                }
            }
        } catch (Exception ignored) {
        }
    }

    interface ServerMessageHandler {
        void handler(String msg);
    }

    abstract static class BaseServerMessageHandler implements ServerMessageHandler {
        private String msgType;


        public String getMsgType() {
            return msgType;
        }

        public void setMsgType(String msgType) {
            this.msgType = msgType;
        }

        @Override
        public void handler(String msg) {
            doHandler(msg);
        }

        public abstract void doHandler(String msg);

    }


    static class FilterServerMessageHandler extends BaseServerMessageHandler {
        private final BaseServerMessageHandler baseServerMessageHandler;
        private final Function<String, Boolean> filterMessage;

        public FilterServerMessageHandler(BaseServerMessageHandler baseServerMessageHandler,
                                          Function<String, Boolean> filterMessage) {
            this.baseServerMessageHandler = baseServerMessageHandler;
            this.filterMessage = filterMessage;
        }

        /**
         * 在设置中设置了不自动刷新，走这个地方进行启动过滤
         */
        @Override
        public void doHandler(String msg) {
            if (filterMessage.apply(getMsgType())) return;
            baseServerMessageHandler.doHandler(msg);
        }
    }

    static class MessageType {
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    class SpringGatewayMessageHandler extends BaseServerMessageHandler {
        @Override
        public void doHandler(String msg) {
            GatewayModel gatewayModel = ObjectMappingUtils.readValue(msg, GatewayModel.class);
            CoolRequestEnvironmentPersistentComponent.State instance = CoolRequestEnvironmentPersistentComponent.getInstance(userProjectManager.getProject());

            for (GatewayModel.Gateway gateway : gatewayModel.getGateways()) {
                RequestEnvironment requestEnvironment = new RequestEnvironment();
                requestEnvironment.setId(gateway.getId());
                requestEnvironment.setEnvironmentName(gateway.getRouteId());
                requestEnvironment.setHostAddress("http://localhost:" + gatewayModel.getPort() + StringUtils.joinUrlPath(gatewayModel.getContext(), addPrefix(gateway.getPrefix())));
                if (instance.getEnvironments().contains(requestEnvironment)) continue;
                instance.getEnvironments().add(requestEnvironment);
            }
            ApplicationManager.getApplication().getMessageBus().syncPublisher(CoolRequestIdeaTopic.ENVIRONMENT_ADDED).event();
        }
    }

    private String addPrefix(String prefix) {
        if (StringUtils.isEmpty(prefix)) return "";
        if (prefix.startsWith("/")) return prefix;
        return "/" + prefix;
    }

    class ProjectStartupServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            ProjectStartupModel projectStartupModel = ObjectMappingUtils.readValue(msg, ProjectStartupModel.class);
            if (projectStartupModel == null) return;
            userProjectManager.addSpringBootApplicationInstance(projectStartupModel.getProjectPort(), projectStartupModel.getPort());
        }
    }

    class RequestReceiveMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            InvokeReceiveModel invokeReceiveModel = ObjectMappingUtils.readValue(msg, InvokeReceiveModel.class);
            if (invokeReceiveModel != null) {
                userProjectManager.onInvokeReceive(invokeReceiveModel);
            }
        }
    }

    class ControllerInfoServerMessageHandler extends BaseServerMessageHandler {
        @Override
        public void doHandler(String msg) {
            RequestMappingModel requestMappingModel = ObjectMappingUtils.readValue(msg, RequestMappingModel.class);
            if (requestMappingModel == null) return;
            ApplicationManager.getApplication().runReadAction(() -> {
                requestMappingModel.getControllers().forEach(controller -> {
                    Module classNameModule = PsiUtils.findClassNameModule(userProjectManager.getProject(), controller.getSimpleClassName());
                    controller.setModuleName(classNameModule == null ? "unknown" : classNameModule.getName());
                    controller.setId(ComponentIdUtils.getMd5(userProjectManager.getProject(), controller));
                    controller.setSpringBootStartPort(requestMappingModel.getPluginPort());
                    if (classNameModule != null) {
                        PsiClass psiClass = PsiUtils.findClassByName(classNameModule.getProject(), classNameModule, controller.getSimpleClassName());
                        if (psiClass != null) {
                            PsiMethod httpMethodInClass = PsiUtils.findHttpMethodInClass(psiClass, controller);
                            if (httpMethodInClass != null) {
                                controller.setOwnerPsiMethod(List.of(httpMethodInClass));

                            }
                        }
                    }
                });

                userProjectManager.addComponent(requestMappingModel.getControllers());
            });

        }
    }

    class ResponseInfoServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            InvokeResponseModel invokeResponseModel = ObjectMappingUtils.readValue(msg, InvokeResponseModel.class);
            if (invokeResponseModel == null) return;
            invokeResponseModel.setId(userProjectManager.getDynamicControllerRawId(invokeResponseModel.getId()));

            ProviderManager.findAndConsumerProvider(RequestManager.class, userProjectManager.getProject(), requestManager -> {
                if (!requestManager.exist(invokeResponseModel.getId())) return;
                userProjectManager.getProject().getMessageBus()
                        .syncPublisher(CoolRequestIdeaTopic.HTTP_RESPONSE)
                        .onResponseEvent(invokeResponseModel.getId(), invokeResponseModel);

            });

        }
    }

    class ClearServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
//            userProjectManager.getProject().getMessageBus()
//                    .syncPublisher(IdeaTopic.DELETE_ALL_REQUEST)
//                    .event();
        }
    }

    class ScheduledServerMessageHandler extends BaseServerMessageHandler {
        @Override
        public void doHandler(String msg) {
            ScheduledModel scheduledModel = ObjectMappingUtils.readValue(msg, ScheduledModel.class);
            if (scheduledModel == null) return;

            ApplicationManager.getApplication().runReadAction(() -> {
                scheduledModel.getScheduledInvokeBeans().forEach(dynamicSpringScheduled -> {
                    Module classNameModule = PsiUtils.findClassNameModule(userProjectManager.getProject(), dynamicSpringScheduled.getClassName());
                    dynamicSpringScheduled.setModuleName(classNameModule == null ? "" : classNameModule.getName());
                    dynamicSpringScheduled.setServerPort(scheduledModel.getPort());
                    dynamicSpringScheduled.setId(ComponentIdUtils.getMd5(userProjectManager.getProject(), dynamicSpringScheduled));
                });
                userProjectManager.addComponent(scheduledModel.getScheduledInvokeBeans());
            });
        }
    }

}
