/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SettingConfig.java is part of Cool Request
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

package com.cool.request.common.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SettingConfig implements Configurable {

    @Nls
    @Override
    public String getDisplayName() {
        return "My Plugin Settings"; // 显示在设置对话框中的名称
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        // 创建你的插件设置界面的 Swing 或 JavaFX 组件，并返回这个组件
        JPanel panel = new JPanel();
        panel.add(new JLabel("Hello, Plugin Settings!"));
        return panel;
    }

    @Override
    public boolean isModified() {
        // 检查用户是否对设置进行了更改
        return false;
    }

    @Override
    public void apply() {
        // 将设置应用到插件中
    }

    @Override
    public void reset() {
        // 重置设置为默认值
    }

    @Override
    public void disposeUIResources() {
        // 在界面被关闭时释放资源
    }
}