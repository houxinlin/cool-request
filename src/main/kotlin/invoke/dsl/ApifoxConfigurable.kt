package invoke.dsl

import com.hxl.plugin.springboot.invoke.plugin.apifox.ApiFoxExport
import com.hxl.plugin.springboot.invoke.plugin.apifox.ApiFoxExportCondition
import com.hxl.plugin.springboot.invoke.state.SettingPersistentState
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.bindText
import javax.swing.JComponent
import javax.swing.JLabel
import kotlin.concurrent.thread

class ApifoxConfigurable(val project: Project) : Configurable {
    private var authorizationTextFieldProperty = ""

    private var openApiTextFieldProperty = ""
    var text: String = ""

    private val state = SettingPersistentState.getInstance().state
    private val apiExport: ApiFoxExport = ApiFoxExport(project)
    private lateinit var authorLabel: JLabel
    private lateinit var openLabel: JLabel
    private lateinit var authorText: JBTextField
    private lateinit var openText: JBTextField
    override fun createComponent(): JComponent {
        authorizationTextFieldProperty = (state.apiFoxAuthorization)
        openApiTextFieldProperty = (state.openApiToken)
        return com.intellij.ui.dsl.builder.panel {
            row("HTTP Authorization") {
                textField().bindText(::authorizationTextFieldProperty) {
                    authorizationTextFieldProperty = it
                }.component.apply {
                    authorText = this
                }
                label("").component.apply { authorLabel = this }
            }

            row("OpenApi Token") {
                textField().bindText(::openApiTextFieldProperty) { openApiTextFieldProperty = it }.component.apply {
                    openText = this
                }
                label("").component.apply { openLabel = this }
            }
            row {
                button("Check") {
                    state.apiFoxAuthorization = authorText.text
                    state.openApiToken = openText.text
                    thread {
                        val apiFoxExportCondition =
                            ApiFoxExportCondition(authorText.text, openText.text)
                        val checkToken = apiExport.checkToken(apiFoxExportCondition)
                        authorLabel.text = (
                                if (checkToken.getOrDefault(ApiFoxExportCondition.KEY_API_FOX_AUTHORIZATION, false)) {
                                    "Success"
                                } else "Invalid Token"
                                )
                        openLabel.text = (
                                if (checkToken.getOrDefault(
                                        ApiFoxExportCondition.KEY_API_FOX_OPEN_AUTHORIZATION,
                                        false
                                    )
                                ) {
                                    "Success"
                                } else "Invalid Open Token"
                                )
                    }
                }
            }
        }

    }

    override fun isModified(): Boolean {
        return true
    }

    override fun apply() {
        state.apiFoxAuthorization = authorizationTextFieldProperty
        state.openApiToken = openApiTextFieldProperty
    }

    override fun getDisplayName(): String {
        return "apifox";
    }
}