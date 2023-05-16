package com.hxl.plugin.springboot.invoke;


import java.io.IOException;

public class Help {
    public static void showDocument(final String uri) {
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
