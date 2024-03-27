/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * flags.kt is part of Cool Request
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
@file:Suppress("EnumEntryName")

package com.cool.request.ui.dsl.layout

// http://www.migcalendar.com/miglayout/mavensite/docs/cheatsheet.pdf


enum class LCFlags {
    /**
     * Puts the layout in a flow-only mode.
     * All components in the flow direction will be put in the same cell and will thus not be aligned with component in other rows/columns.
     * For normal horizontal flow this is the same as to say that all component will be put in the first and only column.
     */
    noGrid,

    /**
     * Puts the layout in vertical flow mode. This means that the next cell is normally below and the next component will be put there instead of to the right. Default is horizontal flow.
     */
    flowY,

    /**
     * Claims all available space in the container for the columns and/or rows.
     * At least one component need to have a "grow" constraint for it to fill the container.
     * The space will be divided equal, though honoring "growPriority".
     * If no columns/rows has "grow" set the grow weight of the components in the rows/columns will migrate to that row/column.
     */
    fill, fillX, fillY,

    debug
}

/**
 * See FAQ in the [docs](https://github.com/JetBrains/intellij-community/tree/master/platform/platform-impl/src/com/intellij/ui/layout).
 * Do not use this flags directly.
 */


enum class CCFlags {
    /**
     * Use [Row.grow] instead.
     */
    grow,

    /**
     * Use [Row.growX] instead.
     */
    growX,

    /**
     * Use [Row.growY] instead.
     */
    growY,

    /**
     * Use [Row.push] instead.
     */
    push,

    /**
     * Use [Row.pushX] instead.
     */
    pushX,

    /**
     * Use [Row.pushY] instead.
     */
    pushY,
}