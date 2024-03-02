package com.cool.request.common.icons

import javax.swing.Icon

object KotlinCoolRequestIcons {
    val MAIN: () -> Icon = { IconLoader.getIcon("/icons/pluginIcon.svg", CoolRequestIcons::class.java) }
    val SETTING:()->Icon ={ IconLoader.getIcon("/icons/svg/large/setting.svg", CoolRequestIcons::class.java);}
}