//package com.cool.request.ui.dsl
//
//import com.cool.request.common.constant.CoolRequestIdeaTopic
//import com.cool.request.common.state.SettingPersistentState
//import com.cool.request.utils.ResourceBundleUtils
//import com.intellij.openapi.application.ApplicationManager
//import com.intellij.openapi.options.BoundSearchableConfigurable
//import com.intellij.openapi.project.Project
//import com.intellij.openapi.ui.DialogPanel
//import com.intellij.ui.dsl.builder.*
//import java.lang.invoke.MethodHandles
//import java.lang.invoke.MethodType
//import javax.swing.DefaultComboBoxModel
//import javax.swing.JLabel
//
///**
// * comment报错不需要管
// */
//class SettingConfigurableDSL2(val project: Project) : BoundSearchableConfigurable("Cool Request", "Cool.Request") {
//    override fun createPanel(): DialogPanel {
//        try {
//            return createPanelInner();
//        } catch (_: Exception) {
//
//        }
//        return DialogPanel().apply {
//            add(JLabel("Error!"))
//        }
//    }
//
//    fun createPanelInner(): DialogPanel {
//        val setting = SettingPersistentState.getInstance().state
//        val language = arrayOf("English", "中文")
//
//        return panel {
//            row {
//                label("Language")
//                comboBox(DefaultComboBoxModel(language)).bindItem({ language[setting.languageValue] },
//                    { setting.languageValue = language.indexOf(it) })
//            }
//            row {
//                checkBox(ResourceBundleUtils.getString("enable.dynamic.refresh"))
//                    .bindSelected({
//                        setting.enableDynamicRefresh
//                    }, {
//                        setting.enableDynamicRefresh = it
//                    }).comment(ResourceBundleUtils.getString("enable.dynamic.refresh.desc"))
//            }
//            row {
//                checkBox(ResourceBundleUtils.getString("auto.goto.code"))
//                    .bindSelected({ setting.autoNavigation },
//                        { setting.autoNavigation = it }).comment(ResourceBundleUtils.getString("auto.goto.code.desc"))
//            }
//            row {
//                checkBox(ResourceBundleUtils.getString("listener.gateway"))
//                    .bindSelected(
//                        { setting.listenerGateway },
//                        { setting.listenerGateway = it })
//                    .comment(ResourceBundleUtils.getString("listener.gateway.desc"))
//            }
//            row {
//                checkBox(ResourceBundleUtils.getString("auto.refresh.component")).bindSelected(
//                    { setting.autoRefreshData },
//                    { setting.autoRefreshData = it })
//                    .comment(ResourceBundleUtils.getString("auto.refresh.component.desc"))
//            }
//            row {
//                checkBox(ResourceBundleUtils.getString("add.quick.send.button")).bindSelected({ setting.addQuickSendButtonOnMethodLeft },
//                    { setting.addQuickSendButtonOnMethodLeft = it })
//            }
//
//
//            group("HTTP Proxy") {
//                row {
//                    checkBox(ResourceBundleUtils.getString("enable.proxy")).bindSelected(
//                        { setting.enableProxy },
//                        { setting.enableProxy = it })
//                }
//                row {
//                    label(ResourceBundleUtils.getString("proxy.setting.tip"))
//                }
//                row {
//                    label("IP")
//                    textField().bindText({ setting.proxyIp }, { setting.proxyIp = it })
//                }
//                row {
//                    label("Port")
//                    spinner(IntRange(0, 65535)).bindIntValue(
//                        { setting.proxyPort }, { setting.proxyPort = it }
//                    )
//                }
//            }
//            group("UI") {
//                row {
//                    checkBox(ResourceBundleUtils.getString("merge.api.request.ui")).bindSelected({
//                        setting.mergeApiAndRequest
//                    }, { setting.mergeApiAndRequest = it })
//                }
//                row {
//                    checkBox("User idea Icon").bindSelected({
//                        setting.userIdeaIcon
//                    }, { setting.userIdeaIcon = it })
//                }
//                val getterSetting: () -> Int = { setting.treeAppearanceMode }
//                val setterSetting: (Int) -> Unit = { x: Int ->
//                    setting.treeAppearanceMode = x
//                }
//
//                val radioButtonSetter: (Any) -> Unit = { x: Any ->
//                    row {
//                        radioButton("Flatten Package", 0)
//                        radioButton("Compact Package", 1)
//                        radioButton("No Package", 2)
//                    }
//                }
//                try {
//                    val buttonsGroupCreate = MethodHandles.lookup().findVirtual(
//                        this::class.java,
//                        "buttonGroup",
//                        MethodType.methodType(
//                            Void.TYPE,
//                            Class.forName("com.intellij.ui.layoutPropertyBinding"),
//                            Class::class.java,
//                            String::class.java,
//                            Boolean::class.java,
//                            Function1::class.java
//                        )
//                    )
//                    val propertyBinding = Class.forName("com.intellij.ui.layout.PropertyBinding")
//                        .getConstructor(Function0::class.java, Function1::class.java)
//                        .newInstance(getterSetting, setterSetting)
//                    buttonsGroupCreate.bindTo(this).invoke(
//                        propertyBinding,
//                        Class.forName("java.lang.Integer"),
//                        "Tree Appearance:",
//                        true,
//                        radioButtonSetter
//                    )
//
//                } catch (e: Exception) {
//                    try {
//                        val buttonsGroupCreate = MethodHandles.lookup().findVirtual(
//                            this::class.java,
//                            "buttonsGroup",
//                            MethodType.methodType(
//                                Class.forName("com.intellij.ui.dsl.builder.ButtonsGroup"),
//                                String::class.java,
//                                Boolean::class.java,
//                                Function1::class.java
//                            )
//                        )
//                        val setter: (Any) -> Unit = { x: Any ->
//                            row {
//                                radioButton("Flatten Package", 0)
//                                radioButton("Compact Package", 1)
//                                radioButton("No Package", 2)
//                            }
//                        }
//                        val buttonsGroupCreateResult =
//                            buttonsGroupCreate.bindTo(this).invoke("Tree Appearance:", true, setter)
//                        val propertyBinding = MethodHandles.lookup().findStatic(
//                            Class.forName("com.intellij.ui.dsl.builder.MutablePropertyKt"),
//                            "MutableProperty",
//                            MethodType.methodType(
//                                Class.forName("com.intellij.ui.dsl.builder.MutableProperty"),
//                                Function0::class.java,
//                                Function1::class.java
//                            )
//                        ).invoke(getterSetting, setterSetting)
//
//                        MethodHandles.lookup().findVirtual(
//                            Class.forName("com.intellij.ui.dsl.builder.ButtonsGroup"), "bind", MethodType.methodType(
//                                Class.forName("com.intellij.ui.dsl.builder.ButtonsGroup"),
//                                Class.forName("com.intellij.ui.dsl.builder.MutableProperty"),
//                                Class::class.java
//                            )
//                        ).bindTo(buttonsGroupCreateResult)
//                            .invoke(propertyBinding, Number::class.java)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//                //2021.3开始用这个，但是到2023.1之后方法就不存在了,无法调用
////                buttonGroup({ setting.treeAppearanceMode }, { setting.treeAppearanceMode = it }, "Tree Appearance:") {
////                    row {
////                        radioButton("Flatten Package", 0)
////                        radioButton("Compact Package", 1)
////                        radioButton("No Package", 2)
////                    }
////                }
//
//                //2022.1开始用这个，之前无法调用
////                buttonsGroup("Tree Appearance:") {
////                    row {
////                        radioButton("Flatten Package", 0)
////                        radioButton("Compact Package", 1)
////                        radioButton("No Package", 2)
////
////                    }
////                }.bind(MutableProperty(getter, setter), Int::class.java)
//            }
//        }
//
//    }
//
//    override fun apply() {
//        super.apply()
//        ApplicationManager.getApplication().messageBus.syncPublisher(CoolRequestIdeaTopic.COOL_REQUEST_SETTING_CHANGE)
//            .event()
//
//    }
//
//}
//
