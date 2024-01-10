package com.hxl.plugin.springboot.invoke.bean.components.scheduled;

import com.hxl.plugin.springboot.invoke.bean.components.DynamicComponent;

public class DynamicSpringScheduled extends SpringScheduled implements DynamicComponent {
    private String springInnerId;

    public String getSpringInnerId() {
        return springInnerId;
    }

    public void setSpringInnerId(String springInnerId) {
        this.springInnerId = springInnerId;
    }

    @Override
    public int getSpringBootStartPort() {
        return getServerPort();
    }
}
