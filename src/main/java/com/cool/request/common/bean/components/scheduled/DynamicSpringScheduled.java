package com.cool.request.common.bean.components.scheduled;

import com.cool.request.common.bean.components.DynamicComponent;
import com.cool.request.common.bean.components.xxljob.XxlJob;
import com.cool.request.component.CanMark;

public class DynamicSpringScheduled extends SpringScheduled implements DynamicComponent, CanMark {
    private String springInnerId;

    public String getSpringInnerId() {
        return springInnerId;
    }

    public void setSpringInnerId(String springInnerId) {
        this.springInnerId = springInnerId;
    }

    public int getSpringBootStartPort() {
        return getServerPort();
    }
}
