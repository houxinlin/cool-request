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
