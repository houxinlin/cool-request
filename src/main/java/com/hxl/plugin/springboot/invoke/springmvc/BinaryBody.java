package com.hxl.plugin.springboot.invoke.springmvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BinaryBody implements Body {
    private String selectFile;

    public BinaryBody(String selectFile) {
        this.selectFile = selectFile;
    }

    @Override
    public byte[] contentConversion() {
        if (selectFile == null) return new byte[0];
        if (Files.exists(Paths.get(selectFile))) {
            try {
                return Files.readAllBytes(Paths.get(selectFile));
            } catch (IOException e) {

            }
        }
        return new byte[0];
    }

    public String getSelectFile() {
        return selectFile;
    }
}
