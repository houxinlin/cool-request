package com.cool.request.view.tool;

import com.cool.request.common.bean.RequestEnvironment;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.constant.CoolRequestIdeaTopic;
import com.cool.request.common.model.GatewayModel;
import com.cool.request.common.state.CoolRequestEnvironmentPersistentComponent;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.common.state.SettingsState;
import com.cool.request.utils.GsonUtils;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.application.ApplicationManager;

import java.util.HashMap;
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
            if ("controller".equals(msgType) || "scheduled".equals(msgType) || "xxl_job".equalsIgnoreCase(msgType)) {
                //如果自动刷新处于关闭状态,并且没有提供手动刷新开关
                return !state.autoRefreshData && !getServerMessageRefreshModelValue();
            }
            if ("spring_gateway".equals(msgType)) {
                return !state.listenerGateway;
            }
            return false;
        };
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
    public synchronized void handlerMessage(String msg) {
        try {
            MessageType messageType = GsonUtils.readValue(msg, MessageType.class);
            if (messageType != null && !StringUtils.isEmpty(messageType.getType())) {
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
            GatewayModel gatewayModel = GsonUtils.readValue(msg, GatewayModel.class);
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


}
