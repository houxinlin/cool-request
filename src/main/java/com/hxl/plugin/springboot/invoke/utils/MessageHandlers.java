package com.hxl.plugin.springboot.invoke.utils;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.model.*;

import java.util.HashMap;
import java.util.Map;

public class MessageHandlers {
    private final UserProjectManager userProjectManager;
    private  final Map<String, ServerMessageHandler> messageHandlerMap = new HashMap<>();

    public MessageHandlers(UserProjectManager userProjectManager) {
        this.userProjectManager = userProjectManager;
        messageHandlerMap.put("controller", new ControllerInfoServerMessageHandler());
        messageHandlerMap.put("response_info", new ResponseInfoServerMessageHandler());
        messageHandlerMap.put("clear", new ClearServerMessageHandler());
        messageHandlerMap.put("scheduled", new ScheduledServerMessageHandler());
        messageHandlerMap.put("startup", new ProjectStartupServerMessageHandler());
        messageHandlerMap.put("invoke_receive", new InvokeMessageReceiveMessageHandler());
    }

    public void handlerMessage(String msg) {
        userProjectManager.removeIfClosePort();
        MessageType messageType = ObjectMappingUtils.readValue(msg, MessageType.class);
        if (!StringUtils.isEmpty(messageType)) {
            if (messageHandlerMap.containsKey(messageType.getType())) {
                messageHandlerMap.get(messageType.getType()).handler(msg);
            }
        }
    }

    interface ServerMessageHandler {
        void handler( String msg);
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

    class ProjectStartupServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            ProjectStartupModel projectStartupModel = ObjectMappingUtils.readValue(msg, ProjectStartupModel.class);
            userProjectManager.onUserProjectStartup(projectStartupModel);
        }
    }

    class InvokeMessageReceiveMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            InvokeReceiveModel invokeReceiveModel = ObjectMappingUtils.readValue(msg, InvokeReceiveModel.class);
            if (invokeReceiveModel != null) {
                userProjectManager.onInvokeReceive(invokeReceiveModel);
            }
        }
    }

    class ControllerInfoServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            RequestMappingModel requestMappingModel = ObjectMappingUtils.readValue(msg, RequestMappingModel.class);
            if (requestMappingModel == null) return;
            userProjectManager.addControllerInfo(requestMappingModel);
        }
    }

    class ResponseInfoServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            InvokeResponseModel invokeResponseModel = ObjectMappingUtils.readValue(msg, InvokeResponseModel.class);
            if (invokeResponseModel == null) return;
            userProjectManager.getProject().getMessageBus()
                    .syncPublisher(IdeaTopic.HTTP_RESPONSE)
                    .onResponseEvent(invokeResponseModel.getId(), invokeResponseModel);
        }
    }

    class ClearServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            userProjectManager.getProject().getMessageBus()
                    .syncPublisher(IdeaTopic.DELETE_ALL_REQUEST)
                    .event();
        }
    }

    class ScheduledServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            ScheduledModel scheduledModel = ObjectMappingUtils.readValue(msg, ScheduledModel.class);
            if (scheduledModel == null) return;
            userProjectManager.addScheduleInfo(scheduledModel);
        }
    }

}
