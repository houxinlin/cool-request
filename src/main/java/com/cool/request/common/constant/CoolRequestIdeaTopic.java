package com.cool.request.common.constant;

import com.cool.request.common.bean.components.Component;
import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.scheduled.BasicScheduled;
import com.cool.request.common.bean.components.scheduled.SpringScheduled;
import com.cool.request.component.http.net.HTTPResponseBody;
import com.cool.request.component.ComponentType;
import com.intellij.util.messages.Topic;

import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.util.List;

public class CoolRequestIdeaTopic {
    public static final Topic<ComponentChooseEventListener> COMPONENT_CHOOSE_EVENT = new Topic<>("ComponentChooseEventListener", ComponentChooseEventListener.class);
    public static final Topic<HttpResponseEventListener> HTTP_RESPONSE = new Topic<>("HTTP_RESPONSE", HttpResponseEventListener.class);
    public static final Topic<HttpResponseEventListener> HTTP_REQUEST_CANCEL = new Topic("HTTP_REQUEST_CANCEL", HttpRequestCancelEventListener.class);
    public static final Topic<DeleteAllDataEventListener> DELETE_ALL_DATA = new Topic<>("DELETE_ALL_DATA", DeleteAllDataEventListener.class);
    public static final Topic<ClearRequestCacheEventListener> CLEAR_REQUEST_CACHE = new Topic<>("CLEAR_REQUEST_CACHE", ClearRequestCacheEventListener.class);
    public static final Topic<SpringScheduledModel> ADD_SPRING_SCHEDULED_MODEL = new Topic<>("ADD_SPRING_SCHEDULED_MODEL", SpringScheduledModel.class);
    public static final Topic<BaseListener> DELETE_ALL_REQUEST = new Topic<>("DELETE_ALL_REQUEST", BaseListener.class);
    public static final Topic<BaseListener> CHANGE_LAYOUT = new Topic<>("CHANGE_LAYOUT", BaseListener.class);
    public static final Topic<ScriptLogListener> SCRIPT_LOG = new Topic<>("SCRIPT_LOG", ScriptLogListener.class);
    public static final Topic<BaseListener> COOL_REQUEST_SETTING_CHANGE = new Topic<BaseListener>("COOL_REQUEST_SETTING_CHANGE", BaseListener.class);
    public static final Topic<BaseListener> ENVIRONMENT_ADDED = new Topic<>("ENVIRONMENT_ADDED", BaseListener.class);
    public static final Topic<BaseListener> ENVIRONMENT_CHANGE = new Topic<>("ENVIRONMENT_CHANGE", BaseListener.class);
    public static final Topic<IdeaFrameEvent> IDEA_FRAME_EVENT_TOPIC = new Topic<>("IDEA_FRAME_EVENT_TOPIC", IdeaFrameEvent.class);
    public static final Topic<BaseListener> REFRESH_CUSTOM_FOLDER = new Topic<>("REFRESH_CUSTOM_FOLDER", BaseListener.class);
    public static final Topic<ComponentAddEvent> COMPONENT_ADD = new Topic<>("COMPONENT_ADD", ComponentAddEvent.class);

    @FunctionalInterface
    public interface ComponentAddEvent {
        public void addComponent(List<? extends Component> components, ComponentType componentType);
    }

    @FunctionalInterface
    public interface ObjectListener {
        void event(Object content);
    }

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

    public interface ClearRequestCacheEventListener {
        default void onClearEvent(List<String> ids) {
        }

        default void onClearAllEvent() {
        }
    }

    public interface SpringRequestMappingModel {
        void addRequestMappingModel(List<? extends Controller> controllers);

        default void restore() {
        }
    }

    public interface HttpResponseEventListener {
        void onResponseEvent(String requestId, HTTPResponseBody httpResponseBody);
    }

    @FunctionalInterface
    public interface HttpRequestCancelEventListener {
        void onCancelEvent(String requestId);
    }

    public interface ComponentChooseEventListener {

        void onChooseEvent(Component component);

        default void refreshEvent(Component component) {
        }
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

    public interface ScheduledChooseEventListener {
        void onChooseEvent(BasicScheduled scheduled);
    }
}
