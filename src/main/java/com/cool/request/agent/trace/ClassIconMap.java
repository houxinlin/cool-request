/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ClassIconMap.java is part of Cool Request
 *
 * License: GPL-3.0+
 *
 * Cool Request is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cool Request is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cool Request.  If not, see <http://www.gnu.org/licenses/>.
 */

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
