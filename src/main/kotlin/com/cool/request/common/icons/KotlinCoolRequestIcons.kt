package com.cool.request.common.icons

import javax.swing.Icon

object KotlinCoolRequestIcons {
    val MAIN: () -> Icon = { IconLoader.getIcon("/icons/pluginIcon.svg", CoolRequestIcons::class.java) }
    val SETTING: () -> Icon = { IconLoader.getIcon("/icons/svg/large/setting.svg", CoolRequestIcons::class.java); }
    val SPRING: () -> Icon = { IconLoader.getIcon("/icons/svg/large/spring.svg", CoolRequestIcons::class.java) }
    val HTTP_REQUEST_PAGE: () -> Icon = { IconLoader.getIcon("/icons/svg/large/http.svg", CoolRequestIcons::class.java) }
    val STATIC_WEB_SERVER: () -> Icon = { IconLoader.getIcon("/icons/svg/large/web_file.svg", CoolRequestIcons::class.java) }
}