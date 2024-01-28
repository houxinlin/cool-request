package com.cool.request.utils;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MessagesWrapperUtils {
    public static void showErrorDialog(@NlsContexts.DialogMessage String message, @NotNull @NlsContexts.DialogTitle String title) {
        SwingUtilities.invokeLater(() -> Messages.showErrorDialog(message, title));
    }

    public static void showInfoMessage(String message, String title) {
        SwingUtilities.invokeLater(() -> Messages.showInfoMessage(message, title));
    }

    public static void showOkCancelDialog(String compileSuccess, String tip, Icon main) {
        SwingUtilities.invokeLater(() -> Messages.showOkCancelDialog(compileSuccess, tip,main));
    }
}
