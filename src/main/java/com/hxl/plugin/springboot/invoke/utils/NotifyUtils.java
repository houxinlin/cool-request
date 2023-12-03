package com.hxl.plugin.springboot.invoke.utils;

import com.intellij.notification.*;
import com.intellij.notification.impl.NotificationGroupEP;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.MessageType;

public class NotifyUtils {

    public static void notification(Project project, String msg) {
        NotificationGroup myCustomNotificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("CoolRequestNotificationGroup");
        myCustomNotificationGroup.createNotification(msg, MessageType.INFO).notify(project);
    }
}
