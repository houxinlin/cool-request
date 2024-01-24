package com.cool.request;

import com.cool.request.bean.components.controller.Controller;
import com.cool.request.bean.components.scheduled.SpringScheduled;
import com.cool.request.model.InvokeResponseModel;
import com.intellij.util.messages.Topic;

import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.util.List;

public class IdeaTopic {
    public static final Topic<ScheduledChooseEventListener> SCHEDULED_CHOOSE_EVENT = new Topic<>("SCHEDULED_CHOOSE_EVENT", ScheduledChooseEventListener.class);
    public static final Topic<ControllerChooseEventListener> CONTROLLER_CHOOSE_EVENT = new Topic<>("CONTROLLER_CHOOSE_EVENT", ControllerChooseEventListener.class);
    public static final Topic<HttpResponseEventListener> HTTP_RESPONSE = new Topic<>("HTTP_RESPONSE", HttpResponseEventListener.class);
    public static final Topic<HttpResponseEventListener> HTTP_REQUEST_CANCEL = new Topic("HTTP_REQUEST_CANCEL", HttpRequestCancelEventListener.class);
    public static final Topic<DeleteAllDataEventListener> DELETE_ALL_DATA = new Topic<>("DELETE_ALL_DATA", DeleteAllDataEventListener.class);
    public static final Topic<ClearRequestCacheEventListener> CLEAR_REQUEST_CACHE = new Topic<>("CLEAR_REQUEST_CACHE", ClearRequestCacheEventListener.class);
    public static final Topic<SpringRequestMappingModel> ADD_SPRING_REQUEST_MAPPING_MODEL = new Topic<>("ADD_SPRING_REQUEST_MAPPING_MODEL", SpringRequestMappingModel.class);
    public static final Topic<SpringScheduledModel> ADD_SPRING_SCHEDULED_MODEL = new Topic<>("ADD_SPRING_SCHEDULED_MODEL", SpringScheduledModel.class);
    public static final Topic<BaseListener> DELETE_ALL_REQUEST = new Topic<>("DELETE_ALL_REQUEST", BaseListener.class);
    public static final Topic<BaseListener> CHANGE_LAYOUT = new Topic<>("CHANGE_LAYOUT", BaseListener.class);
    public static final Topic<ScriptLogListener> SCRIPT_LOG = new Topic<>("SCRIPT_LOG", ScriptLogListener.class);
    public static final Topic<BaseListener> COOL_REQUEST_SETTING_CHANGE = new Topic<BaseListener>("COOL_REQUEST_SETTING_CHANGE", BaseListener.class);
    public static final Topic<BaseListener> ENVIRONMENT_ADDED = new Topic<>("ENVIRONMENT_ADDED", BaseListener.class);
    public static final Topic<BaseListener> ENVIRONMENT_CHANGE = new Topic<>("ENVIRONMENT_CHANGE", BaseListener.class);
    public static final Topic<IdeaFrameEvent> IDEA_FRAME_EVENT_TOPIC = new Topic<>("IDEA_FRAME_EVENT_TOPIC", IdeaFrameEvent.class);


    @FunctionalInterface
    public interface BaseListener {
        void event();
    }

    public interface IdeaFrameEvent {
        default void windowsResizedEvent(ComponentEvent event) {
        }

        default void windowsMovedEvent(ComponentEvent event) {
        }

        default void windowGainedFocus(WindowEvent e) {
        }

        default void windowLostFocus(WindowEvent e) {
        }
    }

    public interface ScriptLogListener {
        void log(String id, String value);

        void clear(String id);
    }

    @FunctionalInterface
    public interface SpringScheduledModel {
        void addSpringScheduledModel(List<? extends SpringScheduled> springScheduled);
    }

    @FunctionalInterface
    public interface ClearRequestCacheEventListener {
        void onClearEvent(String id);
    }

    public interface SpringRequestMappingModel {
        void addRequestMappingModel(List<? extends Controller> controllers);

        default void restore() {
        }
    }

    public interface HttpResponseEventListener {
        void onResponseEvent(String requestId, InvokeResponseModel invokeResponseModel);
    }

    @FunctionalInterface
    public interface HttpRequestCancelEventListener {
        void onCancelEvent(String requestId);
    }

    public interface ControllerChooseEventListener {

        void onChooseEvent(Controller controller);

        default void refreshEvent(Controller controller) {
        }
    }

    @FunctionalInterface
    public interface DeleteAllDataEventListener {
        void onDelete();
    }

    @FunctionalInterface
    public interface ScheduledChooseEventListener {
        void onChooseEvent(SpringScheduled scheduled);
    }
}
