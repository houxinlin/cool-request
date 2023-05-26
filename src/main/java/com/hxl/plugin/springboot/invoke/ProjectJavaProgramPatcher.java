package com.hxl.plugin.springboot.invoke;

import com.hxl.plugin.springboot.invoke.utils.SocketUtils;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.project.Project;
import com.intellij.util.PathsList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

public class ProjectJavaProgramPatcher extends JavaProgramPatcher{
    private void loadJar() {
        if (!Files.exists(Constant.CONFIG_LIB_PATH.getParent())) {
            try {
                Files.createDirectories(Constant.CONFIG_LIB_PATH.getParent());
            } catch (IOException ignored) {
            }
        }
        if (!Files.exists(Constant.CONFIG_LIB_PATH)) {
            URL resource = getClass().getResource(Constant.CLASSPATH_LIB_PATH);
            if (resource == null) {
                return;
            }
            try (InputStream inputStream = resource.openStream();) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                Files.write(Constant.CONFIG_LIB_PATH, buffer.toByteArray());
            } catch (IOException ignored) {
            }
        }
    }
    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        loadJar();

        Project project = ((RunConfiguration) configuration).getProject();
        int port = SocketUtils.getSocketUtils().getPort(project);
        PathsList classPath = javaParameters.getClassPath();
        classPath.add(Constant.CONFIG_LIB_PATH.toString());
        javaParameters.getVMParametersList()
                .addNotEmptyProperty("hxl.spring.invoke.port", String.valueOf(port));

    }
}
