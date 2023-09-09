package invoke.dsl

import com.intellij.openapi.options.Configurable
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class DownloadConfigurable: Configurable {
    override fun createComponent(): JComponent {
        return panel {
            group("Basic Setting") {
                row("存储位置") {
                    textField()
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
        return "下载"
    }
}