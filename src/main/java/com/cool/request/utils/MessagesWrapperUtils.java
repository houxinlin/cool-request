/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * MessagesWrapperUtils.java is part of Cool Request
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

package com.cool.request.utils;

import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.util.function.Consumer;

public class MessagesWrapperUtils {
    public static void showErrorDialogWithI18n(String key) {
        showErrorDialog(ResourceBundleUtils.getString(key));
    }

    public static void showErrorDialog(String message, String title) {
        Runnable runnable = () -> Messages.showErrorDialog(message, title);
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
            return;
        }
        SwingUtilities.invokeLater(runnable);
    }

    public static void showErrorDialog(String message) {
        Runnable runnable = () -> Messages.showErrorDialog(message, ResourceBundleUtils.getString("tip"));
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
            return;
        }
        SwingUtilities.invokeLater(runnable);
    }

    public static void showInfoMessage(String message, String title) {
        Runnable runnable = () -> Messages.showInfoMessage(message, title);
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
            return;
        }
        SwingUtilities.invokeLater(runnable);
    }

    public static void showOkCancelDialog(String compileSuccess, String tip, Icon main) {
        Runnable runnable = () -> Messages.showOkCancelDialog(compileSuccess, tip, main);
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
            return;
        }
        SwingUtilities.invokeLater(runnable);
    }

    public static void showOkCancelDialog(String compileSuccess, String tip, Icon main, Consumer<Integer> consumer) {
        Runnable runnable = () -> consumer.accept(Messages.showOkCancelDialog(compileSuccess, tip, main));
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
            return;
        }
        SwingUtilities.invokeLater(runnable);
    }
}
