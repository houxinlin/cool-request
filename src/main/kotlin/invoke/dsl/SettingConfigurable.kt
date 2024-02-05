package invoke.dsl

import com.cool.request.common.constant.CoolRequestIdeaTopic
import com.cool.request.common.state.SettingPersistentState
import com.cool.request.utils.ResourceBundleUtils
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.layout.InnerCell
import com.intellij.ui.layout.Row
import com.intellij.ui.layout.RowBuilder
import com.intellij.ui.layout.panel
import javax.swing.DefaultComboBoxModel

class SettingConfigurable(val project: Project) : BoundSearchableConfigurable("Cool Request", "Cool.Request") {
    override fun createPanel(): DialogPanel {
        val setting = SettingPersistentState.getInstance().state
        val language = arrayOf("English", "中文")
        return panel {
            afullRow {
                label("Language")
                comboBox(DefaultComboBoxModel(language), {
                    language[setting.languageValue]
                }, {
                    setting.languageValue = language.indexOf(it)
                })
            }
            row {
                checkBox(ResourceBundleUtils.getString("enable.dynamic.refresh"), {
                    setting.enableDynamicRefresh
                }, {
                    setting.enableDynamicRefresh = it
                }).comment(ResourceBundleUtils.getString("enable.dynamic.refresh.desc"))
            }
            row {
                checkBox(ResourceBundleUtils.getString("auto.goto.code"),
                    { setting.autoNavigation },
                    { setting.autoNavigation = it }).comment(ResourceBundleUtils.getString("auto.goto.code.desc"))
            }
            row {
                checkBox(ResourceBundleUtils.getString("listener.gateway"),
                    { setting.listenerGateway },
                    { setting.listenerGateway = it }).comment(ResourceBundleUtils.getString("listener.gateway.desc"))
            }
            row {
                checkBox(ResourceBundleUtils.getString("auto.refresh.component"), { setting.autoRefreshData },
                    { setting.autoRefreshData = it }).comment(
                    ResourceBundleUtils.getString(
                        "auto.refresh.component.desc"
                    )
                )
            }
            titledRow("HTTP Proxy") {

                afullRow {
                    label("IP")
                    textField({ setting.proxyIp }, {
                        setting.proxyIp = it
                    }).withLargeLeftGap()
                }
                afullRow {
                    label("Port")
                    spinner({ setting.proxyPort }, {
                        setting.proxyPort = it
                    }, minValue = 0, maxValue = 65535, step = 1).withLargeLeftGap()
                }
            }

            titledRow("UI") {
                row {
                    checkBox(ResourceBundleUtils.getString("merge.api.request.ui"), {
                        setting.mergeApiAndRequest
                    }, { setting.mergeApiAndRequest = it })
                }
            }
        }
    }

    override fun apply() {
        super.apply()
        ApplicationManager.getApplication().messageBus.syncPublisher(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE)
            .event()

    }
}

fun RowBuilder.afullRow(init: InnerCell.() -> Unit): Row = row { cell(isFullWidth = true, init = init) }