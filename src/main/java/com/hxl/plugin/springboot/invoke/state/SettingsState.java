package com.hxl.plugin.springboot.invoke.state;

import com.intellij.openapi.components.BaseState;

import java.util.Objects;

public class SettingsState  extends BaseState {
    private String apifoxCookie="";

    public String getApifoxCookie() {
        return apifoxCookie;
    }

    public void setApifoxCookie(String apifoxCookie) {
        this.apifoxCookie = apifoxCookie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SettingsState that = (SettingsState) o;
        return Objects.equals(apifoxCookie, that.apifoxCookie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), apifoxCookie);
    }
}
