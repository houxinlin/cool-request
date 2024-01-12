package com.hxl.plugin.springboot.invoke.utils.file;

import com.intellij.openapi.project.Project;

/**
 * todo: to be implemented
 *
 * @author zhangpengjun
 * @date 2024/1/12
 */
public class MacFileChooser extends BasicFileChooser {

    @Override
    public String chooseSingleFile(String basePath, String fileName, Project project) {
        return null;
    }

    @Override
    public String chooseDirector(Project project) {
        return null;
    }

    @Override
    public String chooseFileSavePath(String basePath, String fileName, Project project) {
        return null;
    }

}
