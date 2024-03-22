package com.cool.request.agent.trace;

import com.cool.request.common.icons.CoolRequestIcons;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ClassIconMap {
    public static Map<String, Icon> iconMap =new HashMap<>();
    static {
        iconMap.put("org.springframework", CoolRequestIcons.SPRING_SMALL);
    }

    public static Icon getIcon(String className){
        for (String item : iconMap.keySet()) {
            if (className.startsWith(item))return  iconMap.get(item);
        }
        return CoolRequestIcons.MAIN;
    }
}
