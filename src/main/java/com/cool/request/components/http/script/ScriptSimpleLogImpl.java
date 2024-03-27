/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ScriptSimpleLogImpl.java is part of Cool Request
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

import com.cool.request.components.http.Controller;
import com.cool.request.script.ILog;
import com.cool.request.view.page.IScriptLog;
import com.intellij.openapi.project.Project;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ScriptSimpleLogImpl extends PrintStream implements ILog {
    private final Project project;
    private final Controller controller;

    private final IScriptLog scriptLogPage;

    public ScriptSimpleLogImpl(Project project, Controller controller, IScriptLog scriptLogPage) {
        super(new ByteArrayOutputStream());
        this.project = project;
        this.controller = controller;
        this.scriptLogPage = scriptLogPage;
    }


    @Override
    public void clearLog() {
        scriptLogPage.clear(controller);
    }

    @Override
    public void println(String value) {
        scriptLogPage.println(controller, value);
    }

    @Override
    public void print(String value) {
        scriptLogPage.print(controller, value);
    }
}
