/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * IconLoader.kt is part of Cool Request
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
