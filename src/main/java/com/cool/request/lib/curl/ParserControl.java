/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ParserControl.java is part of Cool Request
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

package com.cool.request.lib.curl;

/**
 * ParserControl is used to control particular behaviour of the parser.
 *
 */
public interface ParserControl {
    /**
     * Called by the parser to determine whether it should stop after last
     * option parsed.
     *
     * @param lastOptionCode
     *            the code of last option parsed
     * @return return true to halt, false to continue parsing
     */
    boolean isFinished(int lastOptionCode);
}