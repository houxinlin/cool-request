package com.cool.request.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BrowseUtils {
    public static void openDirectory(String directoryPath) {
        Desktop desktop = Desktop.getDesktop();

        if (desktop.isSupported(Desktop.Action.OPEN)) {
            try {
                File directory = new File(directoryPath);
                if (!directory.exists()) return;
                desktop.open(directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
