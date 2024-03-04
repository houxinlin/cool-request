package com.cool.request.ui.dsl

import com.cool.request.common.constant.CoolRequestIdeaTopic
import com.cool.request.common.state.SettingPersistentState
import com.cool.request.ui.dsl.layout.*
import com.cool.request.utils.ResourceBundleUtils
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import javax.swing.DefaultComboBoxModel

class CoolRequestSettingConfigurable(val project: Project) :
    BoundSearchableConfigurable("Cool Request", "Cool.Request") {
    override fun createPanel(): DialogPanel {
        val setting = SettingPersistentState.getInstance().state
        val language = arrayOf("English", "中文")

        return panel() {
            titledRow("Base Setting") {
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
                        {
                            setting.listenerGateway = it
                        }).comment(ResourceBundleUtils.getString("listener.gateway.desc"))
                }
                row {
                    checkBox(ResourceBundleUtils.getString("auto.refresh.component"), { setting.autoRefreshData },
                        { setting.autoRefreshData = it }).comment(
                        ResourceBundleUtils.getString(
                            "auto.refresh.component.desc"
                        )
                    )
                }
                row {
                    checkBox(ResourceBundleUtils.getString("add.quick.send.button"),
                        { setting.addQuickSendButtonOnMethodLeft },
                        { setting.addQuickSendButtonOnMethodLeft = it })
                }
            }
            titledRow("UI") {
                row {
                    checkBox(ResourceBundleUtils.getString("merge.api.request.ui"), {
                        setting.mergeApiAndRequest
                    }, { setting.mergeApiAndRequest = it })
                }
                row {
                    checkBox(ResourceBundleUtils.getString("user.idea.icon"),
                        { setting.userIdeaIcon },
                        { setting.userIdeaIcon = it })

                }
                row {
                    buttonGroup("Tree Appearance") {
                        radioButton("Flatten Package", {
                            setting.treeAppearanceMode == 0
                        }, {
                            setting.treeAppearanceMode = 0
                        })
                        radioButton("Compact Package", {
                            setting.treeAppearanceMode == 1
                        }, {
                            setting.treeAppearanceMode = 1
                        })
                        radioButton("No Package", {
                            setting.treeAppearanceMode == 2
                        }, {
                            setting.treeAppearanceMode = 2
                        })
                    }
                }
            }
            titledRow("HTTP Proxy") {
                afullRow {
                    checkBox(ResourceBundleUtils.getString("enable.proxy"),
                        { setting.enableProxy },
                        { setting.enableProxy = it }).component
                }
                afullRow {
                    label(ResourceBundleUtils.getString("proxy.setting.tip"))
                }
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
            titledRow("HTTP Response") {
                row {
                    label(ResourceBundleUtils.getString("http.response.size.limit"))
                    spinner({ setting.maxHTTPResponseSize }, {
                        setting.maxHTTPResponseSize = it
                    }, minValue = 1, maxValue = 10 * 10 * 10, step = 1)
                    label("MB").comment(ResourceBundleUtils.getString("http.response.size.tip"))
                }

            }

        }
    }

    override fun apply() {
        super.apply()
        ApplicationManager.getApplication().messageBus.syncPublisher(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE)
            .event()

    }

    fun RowBuilder.afullRow(init: InnerCell.() -> Unit): Row = row { cell(isFullWidth = true, init = init) }

}
