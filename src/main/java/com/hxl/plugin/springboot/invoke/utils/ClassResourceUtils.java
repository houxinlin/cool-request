package com.hxl.plugin.springboot.invoke.utils;

import com.hxl.plugin.springboot.invoke.Constant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClassResourceUtils {
    public static void copyTo(URL resource,String target){
        if (resource == null) {
            return;
        }
        Path path = Paths.get(target);
        if (!path.getParent().toFile().exists()) path.getParent().toFile().mkdirs();

        try (InputStream inputStream = resource.openStream();) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            Files.write(path, buffer.toByteArray());
        } catch (IOException ignored) {
        }
    }
}
