package com.cool.request.common.icons

import javax.swing.Icon

object KotlinCoolRequestIcons {
    val MAIN: () -> Icon = { IconLoader.getIcon("/icons/pluginIcon.svg", CoolRequestIcons::class.java) }
    val SETTING: () -> Icon = { IconLoader.getIcon("/icons/svg/large/setting.svg", CoolRequestIcons::class.java); }
    val SPRING: () -> Icon = { IconLoader.getIcon("/icons/svg/large/spring.svg", CoolRequestIcons::class.java) }
    val HTTP_REQUEST_PAGE: () -> Icon = { IconLoader.getIcon("/icons/svg/large/http.svg", CoolRequestIcons::class.java) }
    val STATIC_WEB_SERVER: () -> Icon = { IconLoader.getIcon("/icons/svg/large/web_file.svg", CoolRequestIcons::class.java) }
    val MARK: () -> Icon = { IconLoader.getIcon("/icons/svg/mark.svg", CoolRequestIcons::class.java) }
    val REFRESH: () -> Icon = { IconLoader.getIcon("/icons/svg/refresh.svg", CoolRequestIcons::class.java) }

    val DELETE: () -> Icon = { IconLoader.getIcon("/icons/svg/delete.svg", CoolRequestIcons::class.java) }
    val WINDOW: () -> Icon = { IconLoader.getIcon("/icons/svg/window.svg", CoolRequestIcons::class.java) }
    val EXPANDALL: () -> Icon = { IconLoader.getIcon("/icons/svg/expandall.svg", CoolRequestIcons::class.java) }
    val COLLAPSE: () -> Icon = { IconLoader.getIcon("/icons/svg/collapseall.svg", CoolRequestIcons::class.java) }
    val SEARCH: () -> Icon = { IconLoader.getIcon("/icons/svg/search.svg", CoolRequestIcons::class.java) }
    val DEBUG: () -> Icon = { IconLoader.getIcon("/icons/svg/debug.svg", CoolRequestIcons::class.java) }
    val HELP: () -> Icon = { IconLoader.getIcon("/icons/svg/help.svg", CoolRequestIcons::class.java) }
    val CHAT: () -> Icon = { IconLoader.getIcon("/icons/svg/chat.svg", CoolRequestIcons::class.java) }
    val ADD: () -> Icon = { IconLoader.getIcon("/icons/svg/add.svg", CoolRequestIcons::class.java) }
    val SUBTRACTION: () -> Icon = { IconLoader.getIcon("/icons/svg/subtraction.svg", CoolRequestIcons::class.java) }
    val COPY: () -> Icon = { IconLoader.getIcon("/icons/svg/copy.svg", CoolRequestIcons::class.java) }
    val EXPORT: () -> Icon = { IconLoader.getIcon("/icons/svg/export.svg", CoolRequestIcons::class.java) }
    val OPEN_IN_NEW_TAB: () -> Icon = { IconLoader.getIcon("/icons/svg/open_new_tab.svg", CoolRequestIcons::class.java) }
    val CLEAR: () -> Icon = { IconLoader.getIcon("/icons/svg/clear.svg", CoolRequestIcons::class.java) }
    val LIBRARY: () -> Icon = { IconLoader.getIcon("/icons/svg/library.svg", CoolRequestIcons::class.java) }
    val TEMPLATE: () -> Icon = { IconLoader.getIcon("/icons/svg/template.svg", CoolRequestIcons::class.java) }
    val BUILD: () -> Icon = { IconLoader.getIcon("/icons/svg/build.svg", CoolRequestIcons::class.java) }
    val DEPENDENT: () -> Icon = { IconLoader.getIcon("/icons/svg/dependencies.svg", CoolRequestIcons::class.java) }
}