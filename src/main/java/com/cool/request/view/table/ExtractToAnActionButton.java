package com.cool.request.view.table;

import com.cool.request.common.icons.KotlinCoolRequestIcons;
import com.intellij.ui.AnActionButton;

public abstract class ExtractToAnActionButton extends AnActionButton {
    public ExtractToAnActionButton(String text) {
        super(text, KotlinCoolRequestIcons.INSTANCE.getEXPORT().invoke());
    }

}
