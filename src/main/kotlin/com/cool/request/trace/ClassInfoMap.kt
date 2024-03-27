/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ClassInfoMap.kt is part of Cool Request
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

package com.cool.request.trace

import com.cool.request.common.icons.CoolRequestIcons
import javax.swing.Icon

object ClassInfoMap {

    private val classMap = mutableMapOf<String, Info>()

    init {
        classMap["org.springframework"] =
            Info(CoolRequestIcons.SPRING_SMALL, "https://github.com/spring-projects/spring-boot")

        classMap["cn.hutool"] = Info(CoolRequestIcons.HUTOOL_SMALL, "https://github.com/dromara/hutool")
        classMap["org.apache.ibatis"] = Info(CoolRequestIcons.MYBATIS_SMALL, "https://github.com/mybatis/mybatis-3")
    }


    fun getClassIcon(clasName: String): Icon {
        classMap.keys.find { clasName.startsWith(it) }?.run { return classMap[this]!!.icon }
        return CoolRequestIcons.TIMER_SMALL
    }

    fun getClassSite(clasName: String): String? {
        classMap.keys.find { clasName.startsWith(it) }?.run { return classMap[this]!!.site }
        return null
    }

    data class Info(val icon: Icon, val site: String)
}
