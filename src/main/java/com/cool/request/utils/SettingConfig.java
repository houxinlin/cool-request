package com.cool.request.utils;

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