/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SimpleScriptExecute.java is part of Cool Request
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

package com.cool.request.components.http.script;

import com.cool.request.components.http.net.RequestContext;
import com.cool.request.script.ILog;
import com.cool.request.utils.StringUtils;
import com.intellij.openapi.project.Project;

import java.util.List;

public class SimpleScriptExecute implements ScriptExecute {
    private final List<String> requestScript;
    private final List<String> responseScript;
    private final ILog iScriptLog;

    public SimpleScriptExecute(List<String> requestScript, List<String> responseScript, ILog iScriptLog) {
        this.requestScript = requestScript;
        this.responseScript = responseScript;
        this.iScriptLog = iScriptLog;
    }

    @Override
    public boolean execRequest(Project project, Request request) throws Exception {
        JavaCodeEngine javaCodeEngine = new JavaCodeEngine(project);
        for (String script : requestScript) {
            if (StringUtils.isBlank(script)) continue;
            if (!javaCodeEngine.execRequest(request, script)) return false;
        }
        return true;
    }

    @Override
    public void execResponse(Project project, RequestContext requestContext, Response response) {
        JavaCodeEngine javaCodeEngine = new JavaCodeEngine(project);
        for (String script : responseScript) {
            if (StringUtils.isBlank(script)) continue;
            javaCodeEngine.execResponse(response, script, iScriptLog);
        }
    }
}
