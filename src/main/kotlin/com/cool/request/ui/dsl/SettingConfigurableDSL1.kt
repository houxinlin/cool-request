package com.cool.request.ui.dsl

import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

/**
 * Setting代码适配器
 *
 * 兼容UI DSL Version1
 */
class SettingConfigurableDSL1(val project: Project) : BoundSearchableConfigurable("Cool Request", "Cool.Request") {
    private var settingConfigurable: Any

    init {
        val settingConfigurableClass =
            CustomClassLoader(SettingConfigurableDSL1::class.java.classLoader).loadClass("com.cool.request.ui.dsl.SettingConfigurable")
        settingConfigurable = settingConfigurableClass.getConstructor(Project::class.java).newInstance(project)
    }

    override fun createPanel(): DialogPanel {
        val createPanelHandle = MethodHandles.lookup().findVirtual(
            settingConfigurable::class.java,
            "createPanel",
            MethodType.methodType(DialogPanel::class.java)
        )
        return createPanelHandle.bindTo(settingConfigurable).invoke() as DialogPanel;
    }

    override fun apply() {
        super.apply()
        val applyHandler = MethodHandles.lookup().findVirtual(
            settingConfigurable::class.java,
            "apply",
            MethodType.methodType(Void.TYPE)
        )
        applyHandler.bindTo(settingConfigurable).invoke()
    }

    class CustomClassLoader(parentClass: ClassLoader) : ClassLoader(parentClass) {
        override fun findClass(name: String): Class<*>? {
            try {
                val path = "/code/${name.replace(".", "/")}.class"
                val a = SettingConfigurableDSL1::class.java.getResource(path)

                val stream = a?.openStream()
                if (stream != null) {
                    val readAllBytes = stream.readAllBytes()
                    return defineClass(name, readAllBytes, 0, readAllBytes.size)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}

