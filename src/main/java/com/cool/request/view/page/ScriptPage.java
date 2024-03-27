/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ScriptPage.java is part of Cool Request
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

package com.cool.request.view.page;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;


public class ScriptPage extends JBSplitter {
    private final ScriptCodePage scriptCodePage;
    private final ScriptLogPage scriptLogPage;

    public ScriptPage(Project project) {
        this.setOrientation(false);
        this.scriptCodePage = new ScriptCodePage(project);
        this.scriptLogPage = new ScriptLogPage(project);
        this.setFirstComponent(this.scriptCodePage);
        this.setSecondComponent(this.scriptLogPage);
    }

    public ScriptLogPage getScriptLogPage() {
        return scriptLogPage;
    }

    public String getRequestScriptText() {
        return this.scriptCodePage.getRequestScriptText();
    }

    public String getResponseScriptText() {
        return this.scriptCodePage.getResponseScriptText();
    }

    public void setScriptText(String requestScript, String responseScript) {
        this.scriptCodePage.setScriptText(requestScript, responseScript);
    }

    public void setLog(String id, String scriptLog) {
        scriptLogPage.clearAllLog();
        scriptLogPage.setLog(scriptLog);
    }
}
