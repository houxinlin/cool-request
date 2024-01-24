package com.hxl.plugin.springboot.invoke.script;


import com.hxl.plugin.springboot.invoke.utils.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    public ILog log;
    protected Response resp;
    protected Request req;

    public Utils(ILog log, Response response) {
        this.log = log;
        this.resp = response;
    }

    public Utils(ILog log, Request request) {
        this.log = log;
        this.req = request;
    }

    public String readFileAsString(String file) {
        return new String(readFileAsByte(file), StandardCharsets.UTF_8);
    }

    public byte[] readFileAsByte(String file) {
        try {
            Path path = Paths.get(file);
            if (!Files.exists(path)) {
                return null;
            }
            return Files.readAllBytes(path);
        } catch (IOException ignored) {
        }
        return null;
    }

    public boolean writeFile(String target, String content) {
        if (content == null) {
            return false;
        }
        return writeFile(target, content.getBytes());
    }

    public boolean writeFile(String target, byte[] content) {
        return FileUtils.writeFile(target, content);
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
            if (!Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            Files.createFile(path);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }
}
