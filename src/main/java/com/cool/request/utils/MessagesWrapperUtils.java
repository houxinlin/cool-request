package com.cool.request.utils;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Consumer;

public class MessagesWrapperUtils {
    public static void showErrorDialogWithI18n(String key) {
        showErrorDialog(ResourceBundleUtils.getString(key));
    }

    public static void showErrorDialog(@NlsContexts.DialogMessage String message, @NotNull @NlsContexts.DialogTitle String title) {
        SwingUtilities.invokeLater(() -> Messages.showErrorDialog(message, title));
    }

    public static void showErrorDialog(@NlsContexts.DialogMessage String message) {
        SwingUtilities.invokeLater(() -> Messages.showErrorDialog(message, ResourceBundleUtils.getString("tip")));
    }

    public static void showInfoMessage(String message, String title) {
        SwingUtilities.invokeLater(() -> Messages.showInfoMessage(message, title));
    }

    public static void showOkCancelDialog(String compileSuccess, String tip, Icon main) {
        SwingUtilities.invokeLater(() -> Messages.showOkCancelDialog(compileSuccess, tip, main));
    }

    public static void showOkCancelDialog(String compileSuccess, String tip, Icon main, Consumer<Integer> consumer) {
        SwingUtilities.invokeLater(() -> {
            consumer.accept(Messages.showOkCancelDialog(compileSuccess, tip, main));
        });
    }
}
