/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * componentConstraints.kt is part of Cool Request
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
package com.cool.request.ui.dsl.layout.migLayout

import com.cool.request.ui.dsl.layout.CCFlags
import com.cool.request.ui.dsl.layout.SpacingConfiguration
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.ui.SeparatorComponent
import com.intellij.util.ui.JBUI
import net.miginfocom.layout.BoundSize
import net.miginfocom.layout.CC
import net.miginfocom.layout.ConstraintParser
import java.awt.Component
import javax.swing.*
import javax.swing.text.JTextComponent

internal fun overrideFlags(cc: CC, flags: Array<out CCFlags>) {
    for (flag in flags) {
        when (flag) {
            //CCFlags.wrap -> isWrap = true
            CCFlags.grow -> cc.grow()
            CCFlags.growX -> {
                cc.growX(1000f)
            }

            CCFlags.growY -> cc.growY(1000f)

            // If you have more than one component in a cell the alignment keywords will not work since the behavior would be indeterministic.
            // You can however accomplish the same thing by setting a gap before and/or after the components.
            // That gap may have a minimum size of 0 and a preferred size of a really large value to create a "pushing" gap.
            // There is even a keyword for this: "push". So "gapleft push" will be the same as "align right" and work for multi-component cells as well.
            //CCFlags.right -> horizontal.gapBefore = BoundSize(null, null, null, true, null)

            CCFlags.push -> cc.push()
            CCFlags.pushX -> cc.pushX()
            CCFlags.pushY -> cc.pushY()
        }
    }
}

internal class DefaultComponentConstraintCreator(private val spacing: SpacingConfiguration) {
    private val shortTextSizeSpec = ConstraintParser.parseBoundSize("${spacing.shortTextWidth}px!", false, true)
    private val mediumTextSizeSpec =
        ConstraintParser.parseBoundSize("${spacing.shortTextWidth}px::${spacing.maxShortTextWidth}px", false, true)

    val vertical1pxGap: BoundSize = ConstraintParser.parseBoundSize("${JBUI.scale(1)}px!", true, false)

    val horizontalUnitSizeGap = gapToBoundSize(spacing.unitSize, true)

    fun addGrowIfNeeded(cc: CC, component: Component, spacing: SpacingConfiguration) {
        when {
            component is ComponentWithBrowseButton<*> -> {
                // yes, no max width. approved by UI team (all path fields stretched to the width of the window)
                cc.minWidth("${spacing.maxShortTextWidth}px")
                cc.growX()
            }

            component is JTextField && component.columns != 0 ->
                return

            component is JTextComponent || component is SeparatorComponent || component is ComponentWithBrowseButton<*> -> {
                cc.growX()
                //.pushX()
            }

            component is JScrollPane || component.isPanelWithToolbar() -> {
                // no need to use pushX - default pushX for cell is 100. avoid to configure more than need
                cc.grow()
                    .pushY()
            }

            component is JScrollPane -> {
                val view = component.viewport.view
                if (view is JTextArea && view.rows == 0) {
                    // set min size to 2 lines (yes, for some reasons it means that rows should be set to 3)
                    view.rows = 3
                }
            }
        }
    }

    fun applyGrowPolicy(cc: CC, growPolicy: com.cool.request.ui.dsl.layout.GrowPolicy) {
        cc.horizontal.size = when (growPolicy) {
            com.cool.request.ui.dsl.layout.GrowPolicy.SHORT_TEXT -> shortTextSizeSpec
            com.cool.request.ui.dsl.layout.GrowPolicy.MEDIUM_TEXT -> mediumTextSizeSpec
        }
    }
}

private fun Component.isPanelWithToolbar(): Boolean {
    return this is JPanel && componentCount == 1 &&
            (getComponent(0) as? JComponent)?.getClientProperty(ActionToolbar.ACTION_TOOLBAR_PROPERTY_KEY) != null
}