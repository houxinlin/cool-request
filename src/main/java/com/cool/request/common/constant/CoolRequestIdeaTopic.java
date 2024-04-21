/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CoolRequestIdeaTopic.java is part of Cool Request
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

import com.cool.request.agent.trace.TraceFrame;
import com.cool.request.common.bean.components.Component;
import com.cool.request.components.ComponentType;
import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.components.http.net.RequestContext;
import com.intellij.util.messages.Topic;

import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.util.List;

public class CoolRequestIdeaTopic {
    public static final Topic<ComponentChooseEventListener> COMPONENT_CHOOSE_EVENT = new Topic<>("ComponentChooseEventListener", ComponentChooseEventListener.class);
    public static final Topic<HttpResponseEventListener> HTTP_RESPONSE = new Topic<>("HTTP_RESPONSE", HttpResponseEventListener.class);
    public static final Topic<DeleteAllDataEventListener> DELETE_ALL_DATA = new Topic<>("DELETE_ALL_DATA", DeleteAllDataEventListener.class);
    public static final Topic<ClearRequestCacheEventListener> CLEAR_REQUEST_CACHE = new Topic<>("CLEAR_REQUEST_CACHE", ClearRequestCacheEventListener.class);
    public static final Topic<BaseListener> CHANGE_LAYOUT = new Topic<>("CHANGE_LAYOUT", BaseListener.class);
    public static final Topic<BaseListener> COOL_REQUEST_SETTING_CHANGE = new Topic<BaseListener>("COOL_REQUEST_SETTING_CHANGE", BaseListener.class);
    public static final Topic<BaseListener> ENVIRONMENT_ADDED = new Topic<>("ENVIRONMENT_ADDED", BaseListener.class);
    public static final Topic<BaseListener> ENVIRONMENT_CHANGE = new Topic<>("ENVIRONMENT_CHANGE", BaseListener.class);
    public static final Topic<IdeaFrameEvent> IDEA_FRAME_EVENT_TOPIC = new Topic<>("IDEA_FRAME_EVENT_TOPIC", IdeaFrameEvent.class);
    public static final Topic<BaseListener> REFRESH_CUSTOM_FOLDER = new Topic<>("REFRESH_CUSTOM_FOLDER", BaseListener.class);
    public static final Topic<ComponentAddEvent> COMPONENT_ADD = new Topic<>("COMPONENT_ADD", ComponentAddEvent.class);
    public static final Topic<TraceFinishListener> TRACE_FINISH = new Topic<>("TraceFinishListener", TraceFinishListener.class);
    public static final Topic<BaseListener> STATIC_SERVER_CHANGE = new Topic<>("STATIC_SERVER_CHANGE", BaseListener.class);
    @FunctionalInterface
    public interface TraceFinishListener {
        public void traceFinish(List<TraceFrame> traceFrames);
    }

    @FunctionalInterface
    public interface ComponentAddEvent {
        public void addComponent(List<? extends Component> components, ComponentType componentType);
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

    public interface ClearRequestCacheEventListener {
        default void onClearEvent(List<String> ids) {
        }

        default void onClearAllEvent() {
        }
    }

    public interface HttpResponseEventListener {
        void onResponseEvent(String requestId, HTTPResponseBody httpResponseBody, RequestContext requestContext);
    }
    public interface ComponentChooseEventListener {

        void onChooseEvent(Component component);

        default void refreshEvent(Component component) {
        }
    }

    @FunctionalInterface
    public interface DeleteAllDataEventListener {
        void onDelete();
    }
}
