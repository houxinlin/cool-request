package com.cool.request.utils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClassResourceUtils {
    public static boolean exist(String name) {
        URL resource = ClassResourceUtils.class.getResource(name);
        return resource != null;
    }

    public static List<String> readLines(String name) {
        List<String> result = new ArrayList<>();
        URL resource = ClassResourceUtils.class.getResource(name);
        try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(resource.openStream()))) {
            String temp;
            while ((temp = inputStream.readLine()) != null) {
                result.add(temp);
            }
        } catch (IOException ignored) {
        }
        return result;
    }

    public static byte[] read(String name) {
        URL resource = ClassResourceUtils.class.getResource(name);
        try (InputStream inputStream = resource.openStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4096];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        } catch (IOException ignored) {
        }
        return null;
    }

    public static void copyTo(URL resource, String target) {
        if (resource == null) {
            return;
        }
        Path path = Paths.get(target);
        if (!path.getParent().toFile().exists()) path.getParent().toFile().mkdirs();

        try (InputStream inputStream = resource.openStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4096];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            Files.write(path, buffer.toByteArray());
        } catch (IOException ignored) {
        }
    }
}
