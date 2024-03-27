/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * IconCacheManager.kt is part of Cool Request
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

import com.cool.request.common.constant.CoolRequestIdeaTopic
import com.cool.request.common.constant.CoolRequestIdeaTopic.BaseListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import javax.swing.Icon

@Service
class IconCacheManager {

    init {
        ApplicationManager.getApplication().messageBus.connect()
            .subscribe(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE,
                BaseListener { iconCacheMap.clear() })
    }

    private val iconCacheMap: MutableMap<String, Icon> = mutableMapOf()

    fun register(path: String, icon: Icon) {
        iconCacheMap[path] = icon
    }

    fun get(path: String, loadIcon: () -> Icon): Icon {
        return iconCacheMap.computeIfAbsent(path) { loadIcon.invoke() }
    }

    companion object {
        fun getInstance(): IconCacheManager {
            return ApplicationManager.getApplication().getService(IconCacheManager::class.java)
        }

    }
}
