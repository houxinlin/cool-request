package com.cool.request.common.icons;

import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.utils.ClassResourceUtils;

import javax.swing.*;

public class IconLoader {
    public static Icon getIcon(String path, Class<CoolRequestIcons> coolRequestIconsClass) {
        if (SettingPersistentState.getInstance().getState().userIdeaIcon) {
            if (ClassResourceUtils.exist("/icons/idea" + path)) {
                Icon icon = com.intellij.openapi.util.IconLoader.findIcon("/icons/idea" + path, coolRequestIconsClass);
                if (icon != null) {
                    return icon;
                }
            }
        }
        return com.intellij.openapi.util.IconLoader.getIcon(path, coolRequestIconsClass);
    }
}
