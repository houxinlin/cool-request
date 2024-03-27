/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * layoutImpl.kt is part of Cool Request
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

import com.cool.request.ui.dsl.layout.migLayout.MigLayoutBuilder
import com.intellij.openapi.ui.ValidationInfo
import java.awt.Container
import javax.swing.ButtonGroup
import javax.swing.JComponent

@PublishedApi
internal fun createLayoutBuilder(): LayoutBuilder {
    return LayoutBuilder(MigLayoutBuilder(createIntelliJSpacingConfiguration()))
}

@Suppress("DeprecatedCallableAddReplaceWith")
@PublishedApi
@Deprecated(message = "isUseMagic not used anymore")
internal fun createLayoutBuilder(isUseMagic: Boolean) = createLayoutBuilder()

interface LayoutBuilderImpl {
    val rootRow: Row
    fun withButtonGroup(buttonGroup: ButtonGroup, body: () -> Unit)

    fun build(container: Container, layoutConstraints: Array<out LCFlags>)

    val preferredFocusedComponent: JComponent?

    // Validators applied when Apply is pressed
    val validateCallbacks: List<() -> ValidationInfo?>

    // Validators applied immediately on input
    val componentValidateCallbacks: Map<JComponent, () -> ValidationInfo?>

    // Validation applicants for custom validation events
    val customValidationRequestors: Map<JComponent, List<(() -> Unit) -> Unit>>

    val applyCallbacks: Map<JComponent?, List<() -> Unit>>
    val resetCallbacks: Map<JComponent?, List<() -> Unit>>
    val isModifiedCallbacks: Map<JComponent?, List<() -> Boolean>>
}