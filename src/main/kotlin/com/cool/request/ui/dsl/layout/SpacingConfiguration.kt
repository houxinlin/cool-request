/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SpacingConfiguration.kt is part of Cool Request
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

// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import com.intellij.util.ui.JBUI

interface SpacingConfiguration {
    /**
     * Horizontal space between two components (in terms of layout grid - cells).
     *
     * It is space between associated components (somehow relates to each other) - for example, combobox and button to delete items from combobox.
     * Since in most cases components in cells will be associated, it is not a space between two independent components.
     * Horizontal subgroups of components is not supported yet, that's why there is no property to define such space.
     */
    val horizontalGap: Int

    /**
     * Vertical space between two components (in terms of layout grid - rows).
     */
    val verticalGap: Int get() = componentVerticalGap * 2
    val componentVerticalGap: Int

    /**
     * Horizontal gap after label column.
     */
    val labelColumnHorizontalGap: Int

    val largeHorizontalGap: Int
    val largeVerticalGap: Int
    val radioGroupTitleVerticalGap: Int

    val shortTextWidth: Int
    val maxShortTextWidth: Int

    // row comment top gap or gear icon left gap
    val unitSize: Int

    val commentVerticalTopGap: Int

    val dialogTopBottom: Int
    val dialogLeftRight: Int

    /**
     * The size of one indent level (when not overridden by specific control type, e.g. indent of checkbox comment row
     * is defined by checkbox icon size)
     */
    val indentLevel: Int
}

// https://jetbrains.github.io/ui/controls/input_field/#spacing
fun createIntelliJSpacingConfiguration(): SpacingConfiguration {
    return object : SpacingConfiguration {
        override val horizontalGap = JBUI.scale(6)
        override val componentVerticalGap = JBUI.scale(6)
        override val labelColumnHorizontalGap = JBUI.scale(6)
        override val largeHorizontalGap = JBUI.scale(16)
        override val largeVerticalGap = JBUI.scale(20)
        override val radioGroupTitleVerticalGap = JBUI.scale(6 + 2)

        override val shortTextWidth = JBUI.scale(250)
        override val maxShortTextWidth = JBUI.scale(350)

        override val unitSize = JBUI.scale(4)

        override val dialogTopBottom = JBUI.scale(10)
        override val dialogLeftRight = JBUI.scale(12)

        override val commentVerticalTopGap = JBUI.scale(6)

        override val indentLevel: Int
            get() = JBUI.scale(20)
    }
}
