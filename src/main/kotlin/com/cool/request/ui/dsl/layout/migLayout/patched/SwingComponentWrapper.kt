/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SwingComponentWrapper.kt is part of Cool Request
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

import com.intellij.util.ThreeState
import net.miginfocom.layout.ComponentWrapper
import net.miginfocom.layout.ContainerWrapper
import net.miginfocom.layout.LayoutUtil
import net.miginfocom.layout.PlatformDefaults
import java.awt.*
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JTextArea
import javax.swing.SwingUtilities

/** Debug color for component bounds outline.
 */
private val DB_COMP_OUTLINE = Color(0, 0, 200)

internal open class SwingComponentWrapper(private val c: JComponent) : ComponentWrapper {
    private var hasBaseLine = ThreeState.UNSURE
    private var isPrefCalled = false

    private var visualPaddings: IntArray? = null

    override fun getBaseline(width: Int, height: Int): Int {
        var h = height
        val visualPaddings = visualPadding
        if (h < 0) {
            h = c.height
        } else if (visualPaddings != null) {
            h = height + visualPaddings[0] + visualPaddings[2]
        }
        var baseLine = c.getBaseline(if (width < 0) c.width else width, h)
        if (baseLine != -1 && visualPaddings != null) {
            baseLine -= visualPaddings[0]
        }
        return baseLine
    }

    override fun getComponent(): JComponent = c

    override fun getPixelUnitFactor(isHor: Boolean): Float {
        throw RuntimeException("Do not use LPX/LPY")
    }

    override fun getX(): Int = c.x

    override fun getY(): Int = c.y

    override fun getHeight(): Int = c.height

    override fun getWidth(): Int = c.width

    override fun getScreenLocationX(): Int {
        val p = Point()
        SwingUtilities.convertPointToScreen(p, c)
        return p.x
    }

    override fun getScreenLocationY(): Int {
        val p = Point()
        SwingUtilities.convertPointToScreen(p, c)
        return p.y
    }

    override fun getMinimumHeight(sz: Int): Int {
        if (!isPrefCalled) {
            c.preferredSize // To defeat a bug where the minimum size is different before and after the first call to getPreferredSize();
            isPrefCalled = true
        }
        return c.minimumSize.height
    }

    override fun getMinimumWidth(sz: Int): Int {
        if (!isPrefCalled) {
            c.preferredSize // To defeat a bug where the minimum size is different before and after the first call to getPreferredSize();
            isPrefCalled = true
        }
        return c.minimumSize.width
    }

    override fun getPreferredHeight(sz: Int): Int {
        // If the component has not gotten size yet and there is a size hint, trick Swing to return a better height.
        if (c.width == 0 && c.height == 0 && sz != -1) {
            c.setBounds(c.x, c.y, sz, 1)
        }
        return c.preferredSize.height
    }

    override fun getPreferredWidth(sz: Int): Int {
        // If the component has not gotten size yet and there is a size hint, trick Swing to return a better height.
        if (c.width == 0 && c.height == 0 && sz != -1) {
            c.setBounds(c.x, c.y, 1, sz)
        }
        return c.preferredSize.width
    }

    override fun getMaximumHeight(sz: Int): Int = if (c.isMaximumSizeSet) c.maximumSize.height else Integer.MAX_VALUE

    override fun getMaximumWidth(sz: Int): Int = if (c.isMaximumSizeSet) c.maximumSize.width else Integer.MAX_VALUE

    override fun getParent(): ContainerWrapper? {
        val p = c.parent as? JComponent ?: return null
        return SwingContainerWrapper(p)
    }

    override fun getHorizontalScreenDPI(): Int {
        try {
            return c.toolkit.screenResolution
        } catch (e: HeadlessException) {
            return PlatformDefaults.getDefaultDPI()
        }
    }

    override fun getVerticalScreenDPI(): Int {
        try {
            return c.toolkit.screenResolution
        } catch (e: HeadlessException) {
            return PlatformDefaults.getDefaultDPI()
        }
    }

    override fun getScreenWidth(): Int {
        try {
            return c.toolkit.screenSize.width
        } catch (ignore: HeadlessException) {
            return 1024
        }
    }

    override fun getScreenHeight(): Int {
        try {
            return c.toolkit.screenSize.height
        } catch (ignore: HeadlessException) {
            return 768
        }
    }

    override fun hasBaseline(): Boolean {
        if (hasBaseLine == ThreeState.UNSURE) {
            try {
                // do not use component dimensions since it made some components layout themselves to the minimum size
                // and that stuck after that. E.g. JLabel with HTML content and white spaces would be very tall.
                // Use large number but don't risk overflow or exposing size bugs with Integer.MAX_VALUE
                hasBaseLine = ThreeState.fromBoolean(getBaseline(8192, 8192) > -1)
            } catch (ignore: Throwable) {
                hasBaseLine = ThreeState.NO
            }
        }
        return hasBaseLine.toBoolean()
    }

    override fun getLinkId(): String? = c.name

    override fun setBounds(x: Int, y: Int, width: Int, height: Int) {
        c.setBounds(x, y, width, height)
    }

    override fun isVisible(): Boolean = c.isVisible

    override fun getVisualPadding(): IntArray? {
        visualPaddings?.let {
            return it
        }
        return null;
    }

    override fun paintDebugOutline(showVisualPadding: Boolean) {
        if (!c.isShowing) {
            return
        }

        val g = c.graphics as? Graphics2D ?: return

        g.paint = DB_COMP_OUTLINE
        g.stroke = BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10f, floatArrayOf(2f, 4f), 0f)
        g.drawRect(0, 0, width - 1, height - 1)

        if (showVisualPadding) {
            val padding = visualPadding
            if (padding != null) {
                g.color = Color.GREEN
                g.drawRect(
                    padding[1],
                    padding[0],
                    width - 1 - (padding[1] + padding[3]),
                    height - 1 - (padding[0] + padding[2])
                )
            }
        }
    }

    override fun getComponentType(disregardScrollPane: Boolean): Int {
        throw RuntimeException("Should be not called and used")
    }

    override fun getLayoutHashCode(): Int {
        var d = c.maximumSize
        var hash = d.width + (d.height shl 5)

        d = c.preferredSize
        hash += (d.width shl 10) + (d.height shl 15)

        d = c.minimumSize
        hash += (d.width shl 20) + (d.height shl 25)

        if (c.isVisible)
            hash += 1324511

        linkId?.let {
            hash += it.hashCode()
        }

        return hash
    }

    override fun hashCode(): Int = component.hashCode()

    override fun equals(other: Any?): Boolean = other is ComponentWrapper && c == other.component

    override fun getContentBias(): Int {
        return when {
            c is JTextArea || c is JEditorPane || java.lang.Boolean.TRUE == c.getClientProperty("migLayout.dynamicAspectRatio") -> LayoutUtil.HORIZONTAL
            else -> -1
        }
    }
}
