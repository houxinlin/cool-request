/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * AboutDialog.java is part of Cool Request
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

package com.cool.request.view.dialog;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.cool.request.common.icons.CoolRequestIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AboutDialog extends DialogWrapper {
    private JPanel rootPanel;
    private JLabel version;
    private JLabel icon;

    @Override
    protected @Nullable JComponent createCenterPanel() {
        version.setText("v"+ CoolRequestConfigConstant.VERSION);
        icon.setIcon(CoolRequestIcons.MAIN);
        return rootPanel;
    }

    public AboutDialog(@Nullable Project project) {
        super(project);
        init();
    }
}
