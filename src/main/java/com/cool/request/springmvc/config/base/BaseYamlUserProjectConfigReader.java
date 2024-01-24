package com.cool.request.springmvc.config.base;

import com.cool.request.springmvc.config.SpringKey;
import com.cool.request.springmvc.config.UserProjectReader;
import com.cool.request.utils.PsiUtils;
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

public abstract class BaseYamlUserProjectConfigReader<T> implements UserProjectReader<T> {
    private final Project project;
    private final Module module;

    public BaseYamlUserProjectConfigReader(Project project, Module module) {
        this.project = project;
        this.module = module;
    }

    private String generatorPropertiesFileName(String active) {
        return "application-" + active + ".yaml";
    }

    protected String doRead(String name, String key, boolean last) {
        PsiFile[] propertiesFiles = PsiUtils.getUserProjectFile(name, project, module);
        //找不到目标文件
        if (propertiesFiles.length == 0) {
            return null;
        }
        List<PsiFile> properties = Arrays.asList(propertiesFiles)
                .stream()
                .filter(psiFile -> psiFile.getParent().getName().endsWith("resources") && psiFile instanceof YAMLFile).collect(Collectors.toList());
        //目标文件不符合
        if (properties.isEmpty()) {
            return null;
        }

        PsiFile psiFile = properties.get(0);
        String active = getYamlValue(((YAMLFile) psiFile), SpringKey.KEY_ACTIVE);
        if (StringUtil.isNotEmpty(active) && !last) {
            String value = doRead(generatorPropertiesFileName(active), key, true);
            if (value != null) {
                return value;
            }
        }

        String yamlValue = getYamlValue(((YAMLFile) psiFile), key);
        if (yamlValue != null) {
            return yamlValue;
        }
        return null;

    }

    private String getYamlValue(YAMLFile yamlFile, String key) {
        YAMLKeyValue qualifiedKeyInFile = YAMLUtil.getQualifiedKeyInFile(yamlFile, key.split("\\."));
        if (qualifiedKeyInFile != null) {
            return qualifiedKeyInFile.getValueText();
        }
        return null;
    }

}
