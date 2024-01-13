package com.hxl.plugin.springboot.invoke.utils;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MessagesWrapperUtils {
    public static void showErrorDialog(@NlsContexts.DialogMessage String message, @NotNull @NlsContexts.DialogTitle String title) {
        SwingUtilities.invokeLater(() -> Messages.showErrorDialog(message,title));
    }

}
