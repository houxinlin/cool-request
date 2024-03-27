/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * DialogPanel.kt is part of Cool Request
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
package com.cool.request.ui.dsl.layout

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBPanel
import java.awt.LayoutManager
import java.util.function.Supplier
import javax.swing.JComponent
import javax.swing.text.JTextComponent

/**
 * @author yole
 */
class DialogPanel : JBPanel<DialogPanel> {
  var preferredFocusedComponent: JComponent? = null
  var validateCallbacks: List<() -> ValidationInfo?> = emptyList()
  var componentValidateCallbacks: Map<JComponent, () -> ValidationInfo?> = emptyMap()
  var customValidationRequestors: Map<JComponent, List<(() -> Unit) -> Unit>> = emptyMap()
  var applyCallbacks: Map<JComponent?, List<() -> Unit>> = emptyMap()
  var resetCallbacks: Map<JComponent?, List<() -> Unit>> = emptyMap()
  var isModifiedCallbacks: Map<JComponent?, List<() -> Boolean>> = emptyMap()

  private val componentValidationStatus = hashMapOf<JComponent, ValidationInfo>()

  constructor() : super()
  constructor(layout: LayoutManager?) : super(layout)

  fun registerValidators(parentDisposable: Disposable, componentValidityChangedCallback: ((Map<JComponent, ValidationInfo>) -> Unit)? = null) {
    for ((component, callback) in componentValidateCallbacks) {
      val validator = ComponentValidator(parentDisposable).withValidator(Supplier {
        val infoForComponent = callback()
        if (componentValidationStatus[component] != infoForComponent) {
          if (infoForComponent != null) {
            componentValidationStatus[component] = infoForComponent
          }
          else {
            componentValidationStatus.remove(component)
          }
          componentValidityChangedCallback?.invoke(componentValidationStatus)
        }
        infoForComponent
      })
      if (component is JTextComponent) {
        validator.andRegisterOnDocumentListener(component)
      }
      registerCustomValidationRequestors(component, validator)
      validator.installOn(component)
    }
  }

  fun apply() {
    for ((component, callbacks) in applyCallbacks.entries) {
      if (component == null) continue

      val modifiedCallbacks = isModifiedCallbacks.get(component)
      if (modifiedCallbacks.isNullOrEmpty() || modifiedCallbacks.any { it() }) {
        callbacks.forEach { it() }
      }
    }
    applyCallbacks.get(null)?.forEach { it() }
  }

  fun reset() {
    for ((component, callbacks) in resetCallbacks.entries) {
      if (component == null) continue

      callbacks.forEach { it() }
    }
    resetCallbacks.get(null)?.forEach { it() }
  }

  fun isModified(): Boolean {
    return isModifiedCallbacks.values.any { list -> list.any { it() } }
  }

  private fun registerCustomValidationRequestors(component: JComponent, validator: ComponentValidator) {
    for (onCustomValidationRequest in customValidationRequestors.get(component) ?: return) {
      onCustomValidationRequest { validator.revalidate() }
    }
  }
}