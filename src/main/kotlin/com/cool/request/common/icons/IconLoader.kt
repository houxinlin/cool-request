package com.cool.request.common.icons

import com.cool.request.common.state.SettingPersistentState
import com.cool.request.utils.ClassResourceUtils
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object IconLoader {
    @JvmStatic
    fun getIcon(path: String, coolRequestIconsClass: Class<CoolRequestIcons>): Icon {
        return IconCacheManager.getInstance().get(path) {
            if (SettingPersistentState.getInstance().state.userIdeaIcon) {
                if (ClassResourceUtils.exist("/icons/idea$path")) {
                    val icon = IconLoader.findIcon("/icons/idea$path", coolRequestIconsClass)
                    if (icon != null) {
                        return@get icon
                    }
                }
            }
            return@get IconLoader.getIcon(path, coolRequestIconsClass)
        }

    }
}
