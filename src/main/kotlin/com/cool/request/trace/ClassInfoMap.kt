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
