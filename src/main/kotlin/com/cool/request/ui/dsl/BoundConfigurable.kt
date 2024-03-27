/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BoundConfigurable.kt is part of Cool Request
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

import com.cool.request.ui.dsl.layout.DialogPanel
import com.intellij.openapi.Disposable
import com.intellij.openapi.options.*
import com.intellij.openapi.util.ClearableLazyValue
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent

abstract class BoundConfigurable(
    @NlsContexts.ConfigurableName private val displayName: String,
    @NonNls private val helpTopic: String? = null
) : DslConfigurableBase(), Configurable {
    override fun getDisplayName(): String = displayName
    override fun getHelpTopic(): String? = helpTopic
}

/**
 * @see DialogPanelUnnamedConfigurableBase
 */
abstract class DslConfigurableBase : UnnamedConfigurable {
    protected var disposable: Disposable? = null
        private set

    private val panel = object : ClearableLazyValue<DialogPanel>() {
        override fun compute(): DialogPanel {
            if (disposable == null) {
                disposable = Disposer.newDisposable()
            }
            val panel = createPanel()
            panel.registerValidators(disposable!!)
            return panel
        }
    }

    abstract fun createPanel(): DialogPanel

    final override fun createComponent(): JComponent = panel.value

    override fun isModified(): Boolean = panel.value.isModified()

    override fun reset() {
        panel.value.reset()
    }

    override fun apply() {
        panel.value.apply()
    }
    fun  a (): JComponent? {
        return null
    }

    override fun disposeUIResources() {
        disposable?.let {
            Disposer.dispose(it)
            disposable = null
        }

        panel.drop()
    }
}

abstract class BoundSearchableConfigurable(@NlsContexts.ConfigurableName displayName: String, helpTopic: String, private val _id: String = helpTopic)
    : BoundConfigurable(displayName, helpTopic), SearchableConfigurable {
    override fun getId(): String = _id
}
