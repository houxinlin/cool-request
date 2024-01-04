package com.hxl.plugin.springboot.invoke.view.dialog;

public class BaseSetting {
    private  int languageIndex;

    public BaseSetting(int languageIndex) {
        this.languageIndex = languageIndex;
    }

    public BaseSetting() {
    }

    public int getLanguageIndex() {
        return languageIndex;
    }

    public void setLanguageIndex(int languageIndex) {
        this.languageIndex = languageIndex;
    }
}
