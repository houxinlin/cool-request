/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HideableTitledSeparator.kt is part of Cool Request
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

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.NlsContexts
import com.intellij.ui.TitledSeparator
import com.intellij.ui.layout.*
import java.awt.Cursor
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class HideableTitledSeparator(@NlsContexts.Separator title: String) : TitledSeparator(title) {

    private var isExpanded: Boolean = true

    lateinit var row: Row

    fun expand() = update(true)

    fun collapse() = update(false)

    private fun update(expand: Boolean) {
        isExpanded = expand
        row.visible = expand
        row.subRowsVisible = expand
        updateIcon(expand)
    }

    private fun updateIcon(expand: Boolean) {
        val icon = if (expand) AllIcons.General.ArrowDown else AllIcons.General.ArrowRight
        label.icon = icon
        label.disabledIcon = IconLoader.getTransparentIcon(icon, 0.5f)
    }

    init {
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        updateIcon(isExpanded)
        addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                update(!isExpanded)
            }
        })
    }
}