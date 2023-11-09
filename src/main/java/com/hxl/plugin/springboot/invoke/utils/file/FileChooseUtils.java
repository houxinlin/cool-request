package com.hxl.plugin.springboot.invoke.utils.file;

public class FileChooseUtils {
    public static String getFile() {
//        try {
//            if (System.getProperty("os.name").toLowerCase().contains("win")) {
//                return new WindowFileChooser().getFile();
//            }
//        } catch (Exception ignored) {
//        }
        IdeaFileChooser ideaFileChooser = new IdeaFileChooser();
        return ideaFileChooser.getFile();
    }

    public static String getStoragePath(){
        return  new IdeaFileChooser().getStoragePath();
    }
}
