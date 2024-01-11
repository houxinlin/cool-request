package com.hxl.plugin.springboot.invoke.tool;

import com.hxl.plugin.springboot.invoke.Constant;
import com.hxl.plugin.springboot.invoke.utils.ClassResourceUtils;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.project.Project;
import com.intellij.util.PathsList;

import java.io.IOException;
import java.nio.file.Files;

public class ProjectJavaProgramPatcher extends JavaProgramPatcher {
    public ProjectJavaProgramPatcher() {
    }

    private void releaseDependentToUserDir() {
        if (!Files.exists(Constant.CONFIG_LIB_PATH.getParent())) {
            try {
                Files.createDirectories(Constant.CONFIG_LIB_PATH.getParent());
            } catch (IOException ignored) {
            }
        }
        ClassResourceUtils.copyTo(getClass().getResource(Constant.CLASSPATH_JAVAC_LIB_NAME), Constant.CONFIG_JAVAC_PATH.toString());
        ClassResourceUtils.copyTo(getClass().getResource(Constant.CLASSPATH_LIB_PATH), Constant.CONFIG_LIB_PATH.toString());
    }

    /**
     * 程序点击运行的时候会调用这里，主要在classpath上加入start依赖和插件数据监听的端口
     */
    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        releaseDependentToUserDir();
        Project project = ((RunConfiguration) configuration).getProject();
        CoolRequest coolRequest = CoolRequest.getCoolRequest(project);
        PathsList classPath = javaParameters.getClassPath();
        classPath.add(Constant.CONFIG_LIB_PATH.toString());
        ParametersList vmParametersList = javaParameters.getVMParametersList();
        vmParametersList.addNotEmptyProperty("hxl.spring.invoke.port", String.valueOf(coolRequest.getPluginListenerPort()));
    }
}
