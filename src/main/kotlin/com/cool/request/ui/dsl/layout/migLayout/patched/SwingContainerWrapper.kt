/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SwingContainerWrapper.kt is part of Cool Request
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
package com.cool.request.ui.dsl.layout.migLayout.patched

/*
 * License (BSD):
 * ==============
 *
 * Copyright (c) 2004, Mikael Grev, MiG InfoCom AB. (miglayout (at) miginfocom (dot) com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * Neither the name of the MiG InfoCom AB nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * @version 1.0
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 2006-sep-08
 */

import net.miginfocom.layout.ComponentWrapper
import net.miginfocom.layout.ContainerWrapper
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.LayoutManager
import javax.swing.JComponent

/**
 * Debug color for cell outline.
 */
private val DB_CELL_OUTLINE = Color(255, 0, 0)

internal class SwingContainerWrapper(c: JComponent) : SwingComponentWrapper(c), ContainerWrapper {
  override fun getComponents(): Array<ComponentWrapper> {
    val c = component
    return Array(c.componentCount) { index ->
      SwingComponentWrapper(c.getComponent(index) as JComponent)
    }
  }

  override fun getComponentCount(): Int = component.componentCount

  override fun getLayout(): LayoutManager = component.layout

  override fun isLeftToRight(): Boolean = component.componentOrientation.isLeftToRight

  override fun paintDebugCell(x: Int, y: Int, width: Int, height: Int) {
    val c = component
    if (!c.isShowing) {
      return
    }

    val g = c.graphics as? Graphics2D ?: return

    g.stroke = BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10f, floatArrayOf(2f, 3f), 0f)
    g.paint = DB_CELL_OUTLINE
    g.drawRect(x, y, width - 1, height - 1)
  }

  override fun getComponentType(disregardScrollPane: Boolean): Int = ComponentWrapper.TYPE_CONTAINER

  // Removed for 2.3 because the parent.isValid() in MigLayout will catch this instead.
  override fun getLayoutHashCode(): Int = 0
}
