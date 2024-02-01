package com.cool.request.utils;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;

public class NotifyUtils {

    public static void notification(Project project, String msg) {
        NotificationGroup myCustomNotificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("CoolRequestNotificationGroup");
        myCustomNotificationGroup.createNotification(msg, MessageType.INFO).notify(project);
    }
}
