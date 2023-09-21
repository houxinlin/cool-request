package invoke.dsl

import com.hxl.plugin.springboot.invoke.plugin.apifox.ApiFoxExport
import com.hxl.plugin.springboot.invoke.plugin.apifox.ApiFoxExportCondition
import com.hxl.plugin.springboot.invoke.state.SettingPersistentState
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.options.Configurable
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import kotlin.concurrent.thread

class ApipostConfigurable : Configurable {
    private val propertyGraph = PropertyGraph()
    private val authorizationTextFieldProperty = propertyGraph.property("")
    private val authorizationLabelProperty = propertyGraph.property("")

    private val openApiTextFieldProperty = propertyGraph.property("")
    private val openApiTipLabelProperty = propertyGraph.property("")
    private val state = SettingPersistentState.getInstance().state
    private val apiExport: ApiFoxExport = ApiFoxExport()
    override fun createComponent(): JComponent {
        authorizationTextFieldProperty.set(state.apiFoxAuthorization)
        openApiTextFieldProperty.set(state.openApiToken)
        return panel {
            group("Basic Setting") {
                row("HTTP Authorization") {
                    textField().bindText(authorizationTextFieldProperty)
                    label("").bindText(authorizationLabelProperty)
                }
                row("OpenApi Token") {
                    textField().bindText(openApiTextFieldProperty)
                    label("").bindText(openApiTipLabelProperty)
                }
                row {
                    button("Check") {
                        state.apiFoxAuthorization = authorizationTextFieldProperty.get();
                        state.openApiToken = openApiTextFieldProperty.get();
                        thread {
                            val  apiFoxExportCondition = ApiFoxExportCondition(authorizationTextFieldProperty.get(), openApiTextFieldProperty.get())
                            val checkToken = apiExport.checkToken(apiFoxExportCondition)
                            authorizationLabelProperty.set(if (checkToken.getOrDefault(ApiFoxExportCondition.KEY_API_FOX_AUTHORIZATION,false)) {"有效Token"} else "无效Token")
                            openApiTipLabelProperty.set(if (checkToken.getOrDefault(ApiFoxExportCondition.KEY_API_FOX_OPEN_AUTHORIZATION,false)) {"有效Open Token"} else "无效Open Token")
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
        state.apiFoxAuthorization = authorizationTextFieldProperty.get();
        state.openApiToken = openApiTextFieldProperty.get();
    }

    override fun getDisplayName(): String {
        return "apifox";
    }
}