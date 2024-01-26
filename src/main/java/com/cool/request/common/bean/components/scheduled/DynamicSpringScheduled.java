package com.cool.request.common.bean.components.scheduled;

import com.cool.request.common.bean.components.DynamicComponent;

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
