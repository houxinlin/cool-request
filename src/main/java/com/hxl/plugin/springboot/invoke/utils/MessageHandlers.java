package com.hxl.plugin.springboot.invoke.utils;

import com.hxl.plugin.springboot.invoke.IdeaTopic;
import com.hxl.plugin.springboot.invoke.model.InvokeResponseModel;
import com.hxl.plugin.springboot.invoke.model.ProjectStartupModel;
import com.hxl.plugin.springboot.invoke.model.RequestMappingModel;
import com.hxl.plugin.springboot.invoke.model.ScheduledModel;
import com.intellij.openapi.application.ApplicationManager;

import java.util.HashMap;
import java.util.Map;

public class MessageHandlers {
    private final UserProjectManager userProjectManager;
    private static final Map<String, ServerMessageHandler> messageHandlerMap = new HashMap<>();

    public MessageHandlers(UserProjectManager userProjectManager) {
        this.userProjectManager = userProjectManager;
        messageHandlerMap.put("controller", new ControllerInfoServerMessageHandler());
        messageHandlerMap.put("response_info", new ResponseInfoServerMessageHandler());
        messageHandlerMap.put("clear", new ClearServerMessageHandler());
        messageHandlerMap.put("scheduled", new ScheduledServerMessageHandler());
        messageHandlerMap.put("startup", new ProjectStartupServerMessageHandler());
    }

    public void handlerMessage(String msg) {
        userProjectManager.removeIfClosePort();
        System.out.println(msg);
        MessageType messageType = ObjectMappingUtils.readValue(msg, MessageType.class);
        if (!StringUtils.isEmpty(messageType)) {
            if (messageHandlerMap.containsKey(messageType.getType())) {
                messageHandlerMap.get(messageType.getType()).handler(msg);
            }
        }
    }

    interface ServerMessageHandler {
        void handler(String msg);
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

    class ControllerInfoServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            RequestMappingModel requestMappingModel = ObjectMappingUtils.readValue(msg, RequestMappingModel.class);
            if (requestMappingModel == null) return;
            userProjectManager.addControllerInfo(requestMappingModel);
        }
    }

    static class ResponseInfoServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            InvokeResponseModel invokeResponseModel = ObjectMappingUtils.readValue(msg, InvokeResponseModel.class);
            if (invokeResponseModel == null) return;
            ApplicationManager.getApplication().getMessageBus()
                    .syncPublisher(IdeaTopic.HTTP_RESPONSE)
                    .onResponseEvent(invokeResponseModel.getId(), invokeResponseModel);
        }
    }

    static class ClearServerMessageHandler implements ServerMessageHandler {
        @Override
        public void handler(String msg) {
            ApplicationManager.getApplication().getMessageBus()
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
