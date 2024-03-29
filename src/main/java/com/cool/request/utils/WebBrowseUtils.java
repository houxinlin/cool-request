/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * WebBrowseUtils.java is part of Cool Request
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

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class WebBrowseUtils {
    public static void browse(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (Exception e) {
            e.printStackTrace();
            nativeCommand(url);
        }
    }

    public static void nativeCommand(final String uri) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Mac OS")) {
                Runtime.getRuntime().exec(new String[]{"open", uri});
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", uri});
            } else { //assume Unix or Linux
                new Thread(() -> {
                    try {
                        for (String[] browser : commands) {
                            try {
                                String[] command = new String[browser.length];
                                for (int i = 0; i < browser.length; i++)
                                    if (browser[i].equals("$1"))
                                        command[i] = uri;
                                    else
                                        command[i] = browser[i];
                                if (Runtime.getRuntime().exec(command).waitFor() == 0)
                                    return;
                            } catch (IOException ignored) {
                            }
                        }
                        String browsers = System.getenv("BROWSER") == null ? "x-www-browser:firefox:iceweasel:seamonkey:mozilla:" +
                                "epiphany:konqueror:chromium:chromium-browser:google-chrome:" +
                                "www-browser:links2:elinks:links:lynx:w3m" : System.getenv("BROWSER");
                        for (String browser : browsers.split(":")) {
                            try {
                                Runtime.getRuntime().exec(new String[]{browser, uri});
                                return;
                            } catch (IOException ignored) {
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String[][] commands = new String[][]{
            {"xdg-open", "$1"},
            {"gio", "open", "$1"},
            {"gvfs-open", "$1"},
            {"gnome-open", "$1"},  // Gnome
            {"mate-open", "$1"},  // Mate
            {"exo-open", "$1"},  // Xfce
            {"enlightenment_open", "$1"},  // Enlightenment
            {"gdbus", "call", "--session", "--dest", "org.freedesktop.portal.Desktop",
                    "--object-path", "/org/freedesktop/portal/desktop",
                    "--method", "org.freedesktop.portal.OpenURI.OpenURI",
                    "", "$1", "{}"},  // Flatpak
            {"open", "$1"},  // Mac OS fallback
            {"rundll32", "url.dll,FileProtocolHandler", "$1"},  // Windows fallback
    };
}
