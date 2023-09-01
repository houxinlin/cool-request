package invoke.dsl

import com.hxl.plugin.springboot.invoke.state.Settings
import com.hxl.plugin.springboot.invoke.state.SettingsState
import com.intellij.diff.tools.util.base.TextDiffSettingsHolder
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diff.DiffBundle
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.options.Configurable
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.labelTable
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.jbTextField
import com.jetbrains.rd.framework.base.deepClonePolymorphic
import javax.swing.JComponent
import javax.swing.JLabel

class ApifoxConfiburable: Configurable {
    private val propertyGraph = PropertyGraph()
    private val textAreaOverallFeedbackProperty = propertyGraph.property("")
    private val state =Settings.getInstance().state

    override fun createComponent(): JComponent {
        textAreaOverallFeedbackProperty.set(state.apifoxCookie)
        return panel {
            group("Basic Setting") {
                row("Cookie") {
                    textField().bindText(textAreaOverallFeedbackProperty)
                }
                row {
                    button("Check") {
                        state.apifoxCookie=textAreaOverallFeedbackProperty.get();
                        Settings.getInstance().loadState( SettingsState().apply {
                            this.apifoxCookie =textAreaOverallFeedbackProperty.get()
                        })
                    }
                }
            }
        }
    }

    override fun isModified(): Boolean {
        return true
    }

    override fun apply() {
    }

    override fun getDisplayName(): String {
        return "apifox";
    }
}