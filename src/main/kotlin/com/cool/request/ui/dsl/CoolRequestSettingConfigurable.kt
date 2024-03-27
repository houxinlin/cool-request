/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * CoolRequestSettingConfigurable.kt is part of Cool Request
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

package com.cool.request.ui.dsl

import com.cool.request.common.constant.CoolRequestConfigConstant
import com.cool.request.common.constant.CoolRequestIdeaTopic
import com.cool.request.common.state.SettingPersistentState
import com.cool.request.ui.dsl.layout.*
import com.cool.request.utils.BrowseUtils
import com.cool.request.utils.ResourceBundleUtils
import com.cool.request.view.widget.KeymapPanel
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.actionSystem.Shortcut
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.keymap.Keymap
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.project.Project
import javax.swing.DefaultComboBoxModel


class CoolRequestSettingConfigurable(val project: Project) :
    BoundSearchableConfigurable("Cool Request", "Cool.Request") {
    private lateinit var keymapPanel: KeymapPanel
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
                    cell {
                        buttonGroup("Tree Appearance") {
                            radioButton("Flatten Package", {
                                setting.treeAppearanceMode == 0
                            }, {
                                setting.treeAppearanceMode = 0
                            }).withLeftGap(10)
                            radioButton("Compact Package", {
                                setting.treeAppearanceMode == 1
                            }, {
                                setting.treeAppearanceMode = 1
                            }).withLeftGap(10)
                            radioButton("No Package", {
                                setting.treeAppearanceMode == 2
                            }, {
                                setting.treeAppearanceMode = 2
                            }).withLeftGap(10)
                        }
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
            titledRow("keymap") {
                row {
                    component(KeymapPanel().apply {
                        keymapPanel = this;
                    }).onIsModified { keymapPanel.newKeyStroke != null }
                        .comment(ResourceBundleUtils.getString("search.shortcut.key"))
                }
            }
            titledRow("HTTP Request") {
                row {
                    cell {
                        label(ResourceBundleUtils.getString("request.timeout"))
                        spinner({ setting.requestTimeout }, {
                            setting.requestTimeout = it
                        }, minValue = 0, maxValue = 100000, step = 1).withLeftGap(5)
                        label("s").withLeftGap(5).comment(ResourceBundleUtils.getString("set.request.timeout"))
                    }

                }
                row {
                    checkBox(ResourceBundleUtils.getString("request.add.browser"),
                        { setting.requestAddUserAgent },
                        { setting.requestAddUserAgent = it })
                }
                row {
                    textField({ setting.userAgent }, { setting.userAgent = it }).comment("HTTP User-Agent value")
                }
            }
            titledRow("HTTP Response") {
                row {
                    cell {
                        label(ResourceBundleUtils.getString("http.response.size.limit"))
                        spinner({ setting.maxHTTPResponseSize }, {
                            setting.maxHTTPResponseSize = it
                        }, minValue = 1, maxValue = 10 * 10 * 10, step = 1).withLeftGap(5)
                        label("MB").withLeftGap(5).comment(ResourceBundleUtils.getString("http.response.size.tip"))
                    }
                }

            }
            titledRow("Other") {
                row {
                    checkBox(ResourceBundleUtils.getString("listener.curl"),
                        { setting.listenerCURL },
                        { setting.listenerCURL = it })
                }
            }
            titledRow("Debug") {
                row {
                    cell {
                        button("Open Log") {
                            CoolRequestConfigConstant.LOG_PATH.apply {
                                if (!this.toFile().exists()) {
                                    this.toFile().mkdirs()
                                }
                            }
                            BrowseUtils.openDirectory(CoolRequestConfigConstant.LOG_PATH.toString())
                        }
                    }
                }

            }
        }
    }

    override fun apply() {
        super.apply()
        val state = SettingPersistentState.getInstance().state
        keymapPanel.newKeyStroke?.run {
            state.searchApiKeyCode = this.keyCode
            state.searchApiModifiers = this.modifiers
            val shortcut: Shortcut = KeyboardShortcut(this, null)
            val activeKeymap: Keymap = KeymapManager.getInstance().activeKeymap
            activeKeymap.removeAllActionShortcuts("com.cool.request.HotkeyAction")
            activeKeymap.addShortcut("com.cool.request.HotkeyAction", shortcut)
        }
        ApplicationManager.getApplication().messageBus.syncPublisher(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE)
            .event()

    }


    fun RowBuilder.afullRow(init: InnerCell.() -> Unit): Row = row { cell(isFullWidth = true, init = init) }

}
