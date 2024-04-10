/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ProjectJavaProgramPatcher.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cool.request.view.tool;

import com.cool.request.common.ResourceDecompressor;
import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.state.SettingPersistentState;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.project.Project;
import com.intellij.util.PathsList;

public class ProjectJavaProgramPatcher extends JavaProgramPatcher {
    public ProjectJavaProgramPatcher() {
    }

    /**
     * 程序点击运行的时候会调用这里，主要在classpath上加入start依赖和插件数据监听的端口
     */
    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {

        ResourceDecompressor.getJavaLibDecompressor().decompressor();

        Project project = ((RunConfiguration) configuration).getProject();
        CoolRequest coolRequest = CoolRequest.getInstance(project);
        PathsList classPath = javaParameters.getClassPath();
        ParametersList vmParametersList = javaParameters.getVMParametersList();

        vmParametersList.addNotEmptyProperty("hxl.spring.invoke.port", String.valueOf(coolRequest.getPluginListenerPort()));

        if (SettingPersistentState.getInstance().getState().enableDynamicRefresh) {
            if (!classPath.getPathList().contains(CoolRequestConfigConstant.CONFIG_LIB_PATH.toString())) {
                classPath.add(CoolRequestConfigConstant.CONFIG_LIB_PATH.toString());
            }
        }
        if (SettingPersistentState.getInstance().getState().enabledTrace) {
            String agentParam = "-javaagent:" + CoolRequestConfigConstant.CONFIG_AGENT_LIB_PATH + "=test";
            if (!vmParametersList.hasParameter(agentParam)) {
                vmParametersList.add(agentParam);
            }
        }
    }
}
