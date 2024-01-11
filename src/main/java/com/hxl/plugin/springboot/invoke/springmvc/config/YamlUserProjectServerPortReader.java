package com.hxl.plugin.springboot.invoke.springmvc.config;

import com.hxl.plugin.springboot.invoke.springmvc.config.base.BaseYamlUserProjectConfigReader;
import com.hxl.plugin.springboot.invoke.utils.PsiUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class YamlUserProjectServerPortReader extends BaseYamlUserProjectConfigReader<Integer> {
    public YamlUserProjectServerPortReader(Project project, Module module) {
        super(project, module);
    }

    @Override
    public Integer read() {
        String value = doRead("application.yaml", SpringKey.KEY_NAME, false);
        if (value != null) {
            return Integer.valueOf(value);
        }
        return null;
    }
}
