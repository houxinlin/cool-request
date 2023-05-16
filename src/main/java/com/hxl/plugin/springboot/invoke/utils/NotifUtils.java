package com.hxl.plugin.springboot.invoke.utils;

import com.intellij.notification.*;

public class NotifUtils {
    public static void notify(String title, String message, NotificationType type) {
        Notification notification = new Notification("MyPlugin", title, message, type);
        Notifications.Bus.notify(notification);
    }
}
