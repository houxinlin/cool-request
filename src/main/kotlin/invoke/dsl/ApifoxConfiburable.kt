package invoke.dsl

import com.hxl.plugin.springboot.invoke.plugin.apifox.ApiFoxExport
import com.hxl.plugin.springboot.invoke.state.SettingPersistentState
import com.hxl.plugin.springboot.invoke.utils.NotifyUtils
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.options.Configurable
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import kotlin.concurrent.thread

class ApifoxConfiburable : Configurable {
    private val propertyGraph = PropertyGraph()
    private val cookieTextFieldProperty = propertyGraph.property("")
    private val tipLabelProperty = propertyGraph.property("")

    private val openApiTextFieldProperty = propertyGraph.property("")
    private val openApiTipLabelProperty = propertyGraph.property("")
    private val state = SettingPersistentState.getInstance().state
    private val apiExport: ApiFoxExport = ApiFoxExport()
    override fun createComponent(): JComponent {
        cookieTextFieldProperty.set(state.apifoxCookie)
        return panel {
            group("Basic Setting") {
                row("HTTP Authorization") {
                    textField().bindText(cookieTextFieldProperty)
                    label("").bindText(tipLabelProperty)
                }
                row("OpenApi Token") {
                    textField().bindText(openApiTextFieldProperty)
                    label("").bindText(openApiTipLabelProperty)
                }
                row {
                    button("Check") {
                        state.apifoxCookie = cookieTextFieldProperty.get();
                        thread {
                            NotifyUtils.notification(if (apiExport.checkCookie(cookieTextFieldProperty.get())) {"验证成功"} else "验证失败")
                            tipLabelProperty.set(if (apiExport.checkCookie(cookieTextFieldProperty.get())) {"有效Token"} else "无效Token")
                        }
                    }
                }
            }
        }
    }

    override fun isModified(): Boolean {
        return true
    }

    override fun apply() {
        state.apifoxCookie = cookieTextFieldProperty.get();
    }

    override fun getDisplayName(): String {
        return "apifox";
    }
}