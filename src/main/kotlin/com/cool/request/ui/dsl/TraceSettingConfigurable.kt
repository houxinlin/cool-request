package com.cool.request.ui.dsl

import com.cool.request.common.constant.CoolRequestIdeaTopic
import com.cool.request.common.state.SettingPersistentState
import com.cool.request.ui.dsl.layout.*
import com.cool.request.utils.ResourceBundleUtils
import com.cool.request.view.widget.KeymapPanel
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project


class TraceSettingConfigurable(val project: Project) :
    BoundSearchableConfigurable("Trace", "Cool.Request") {
    private lateinit var keymapPanel: KeymapPanel
    override fun createPanel(): DialogPanel {
        val setting = SettingPersistentState.getInstance().state

        return panel() {
            titledRow("Base Setting") {
                row {
                    checkBox(ResourceBundleUtils.getString("enabled.trace"), {
                        setting.enabledTrace
                    }, {
                        setting.enabledTrace = it
                    })
                }
                row {
                    checkBox(ResourceBundleUtils.getString("use.byte.code.cache"), {
                        setting.userByteCodeCache
                    }, {
                        setting.userByteCodeCache = it
                    }).comment(ResourceBundleUtils.getString("use.byte.code.cache.tip"))
                }
                afullRow {
                    label(ResourceBundleUtils.getString("trace.max.depth"))
                    spinner({ setting.maxTraceDepth }, { setting.maxTraceDepth = it }, 1, 100)
                        .comment(ResourceBundleUtils.getString("trace.depth.tip")).withLargeLeftGap()
                }
                afullRow {
                    label(ResourceBundleUtils.getString("time.consumption.threshold"))
                    spinner(
                        { setting.timeConsumptionThreshold },
                        { setting.timeConsumptionThreshold = it },
                        1,
                        Int.MAX_VALUE
                    )
                        .comment(ResourceBundleUtils.getString("time.consumption.threshold.tip")).withLargeLeftGap()
                    label("ms").withLargeLeftGap()
                }
                row {
                    checkBox(ResourceBundleUtils.getString("trace.mybatis"), {
                        setting.traceMybatis
                    }, {
                        setting.traceMybatis = it
                    })
                }
                row {
                    checkBox("Log", {
                        setting.useTraceLog
                    }, {
                        setting.useTraceLog = it
                    }).comment("Print trace log")
                }
            }
        }
    }

    fun RowBuilder.afullRow(init: InnerCell.() -> Unit): Row = row { cell(isFullWidth = true, init = init) }
    override fun apply() {
        super.apply()
        ApplicationManager.getApplication().messageBus.syncPublisher(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE)
            .event()

    }
}
