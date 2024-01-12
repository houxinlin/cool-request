package com.hxl.plugin.springboot.invoke.utils.file;

/**
 * @author zhangpengjun
 * @date 2024/1/12
 */
public class SystemOsUtils {

    public static boolean isWindows() {
        String osName = getOsName();
        return osName != null && osName.startsWith("Windows");
    }

    public static boolean isMacOs() {
        String osName = getOsName();
        return osName != null && osName.startsWith("Mac");
    }

    public static boolean isLinux() {
        String osName = getOsName();
        return (osName != null && osName.startsWith("Linux")) || (!isWindows() && !isMacOs());
    }

    public static String getOsName() {
        return System.getProperty("os.name");
    }

}
