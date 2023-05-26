package com.hxl.plugin.springboot.invoke.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class NotifyUtils {
    private static final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("com.hxl.plugin.scheduled-invoke", NotificationDisplayType.BALLOON, true);

    public static void notification(Project project, String msg) {
        final Notification notification = NOTIFICATION_GROUP.createNotification(msg, NotificationType.INFORMATION);
        notification.notify(project);
    }
}
