package com.hxl.plugin.springboot.invoke.utils.file;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.utils.ClassResourceUtils;
import com.intellij.openapi.project.Project;

import java.net.URL;

public class WindowFileChooser  extends BasicFileChooser{
    @Override
    public String getFile(Project project) {
        loadSo();
        return NativeWindowDialogUtils.openFileSelectDialog();
    }
    private void loadSo(){
        URL resource = NativeWindowDialogUtils.class.getResource(Constant.CLASSPATH_WINDOW_SO_LIB_PATH);
        ClassResourceUtils.copyTo(resource, Constant.CONFIG_SO_PATH.toString());
        System.load(Constant.CONFIG_SO_PATH.toString());
    }

    @Override
    public String getSavePath(String basePath, String fileName, Project project) {
        return null;
    }

    @Override
    public String getStoragePath(Project project) {
        return null;
    }
}
