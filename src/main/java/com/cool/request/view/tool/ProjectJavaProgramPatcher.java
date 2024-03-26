package com.cool.request.view.tool;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.utils.ClassResourceUtils;
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
        if (!Files.exists(CoolRequestConfigConstant.CONFIG_LIB_PATH.getParent())) {
            try {
                Files.createDirectories(CoolRequestConfigConstant.CONFIG_LIB_PATH.getParent());
            } catch (IOException ignored) {
            }
        }
        ClassResourceUtils.copyTo(getClass().getResource(CoolRequestConfigConstant.CLASSPATH_JAVAC_LIB_NAME), CoolRequestConfigConstant.CONFIG_JAVAC_PATH.toString());
        ClassResourceUtils.copyTo(getClass().getResource(CoolRequestConfigConstant.CLASSPATH_LIB_PATH), CoolRequestConfigConstant.CONFIG_LIB_PATH.toString());
        ClassResourceUtils.copyTo(getClass().getResource(CoolRequestConfigConstant.COOL_REQUEST_AGENT), CoolRequestConfigConstant.CONFIG_AGENT_LIB_PATH.toString());
    }

    /**
     * 程序点击运行的时候会调用这里，主要在classpath上加入start依赖和插件数据监听的端口
     */
    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {

        releaseDependentToUserDir();
        Project project = ((RunConfiguration) configuration).getProject();
        CoolRequest coolRequest = CoolRequest.getInstance(project);
        PathsList classPath = javaParameters.getClassPath();
        ParametersList vmParametersList = javaParameters.getVMParametersList();

        vmParametersList.addNotEmptyProperty("hxl.spring.invoke.port", String.valueOf(coolRequest.getPluginListenerPort()));

        if (SettingPersistentState.getInstance().getState().enableDynamicRefresh) {
            classPath.add(CoolRequestConfigConstant.CONFIG_LIB_PATH.toString());
        }
        if (SettingPersistentState.getInstance().getState().enabledTrace) {
            vmParametersList.add("-javaagent:" + CoolRequestConfigConstant.CONFIG_AGENT_LIB_PATH);
        }
    }
}
