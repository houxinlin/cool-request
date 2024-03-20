package com.cool.request.components.scheduled;

import com.cool.request.common.bean.components.DynamicComponent;
import com.cool.request.components.CanMark;

import java.io.Serializable;

public class DynamicXxlJobScheduled extends XxlJobScheduled
        implements DynamicComponent, CanMark, Serializable {
    private static final long serialVersionUID = 1000000000;

    public int getSpringBootStartPort() {
        return getServerPort();
    }
}
