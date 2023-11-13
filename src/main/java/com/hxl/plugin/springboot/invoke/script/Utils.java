package com.hxl.plugin.springboot.invoke.script;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    public String readFileAsString(String file) {
        return new String(readFileAsByte(file));
    }

    public byte[] readFileAsByte(String file) {
        try {
            Path path = Paths.get(file);
            if (!Files.exists(path)) return null;
            return Files.readAllBytes(path);
        } catch (IOException ignored) {
        }
        return null;
    }

    public boolean writeFile(String target, String content) {
        if (content == null) return false;
        return writeFile(target, content.getBytes());
    }

    public boolean writeFile(String target, byte[] content) {
        Path path = Paths.get(target);
        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException ignored) {
            }
        }
        try {
            Files.write(path, content);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean createDirs(String path) {
        try {
            Files.createDirectories(Paths.get(path));
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    public boolean createFile(String target) {
        try {
            Path path = Paths.get(target);
            Path parent = path.getParent();
            if (!Files.exists(parent)) Files.createDirectories(parent);
            Files.createFile(path);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    public void print(Object obj) {
        System.out.print(obj);
    }

    public void println(Object obj) {
        System.out.println(obj);
    }

    public void saveResponseBody(String path, Response response) {
        byte[] body = response.getBody();
        if (body == null) body = new byte[]{0};
        writeFile(path, body);
    }

    public void saveResponse(String path, Response response) {
        StringBuilder bodyBuffer = new StringBuilder();
        bodyBuffer.append(response.getHeaderAsString()).append("\n");
        byte[] body = response.getBody();
        if (body == null) body = new byte[]{0};
        bodyBuffer.append(new String(body));
        writeFile(path, bodyBuffer.toString());
    }

}
