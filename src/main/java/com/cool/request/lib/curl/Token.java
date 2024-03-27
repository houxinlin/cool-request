/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * Token.java is part of Cool Request
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
 * Token handles tokenizing the CLI arguments
 */
class Token {
    /** Type for a separator token */
    public static final int TOKEN_SEPARATOR = 0;

    /** Type for a text token */
    public static final int TOKEN_STRING = 1;

    private final int type;

    private final String value;

    /**
     * New Token object with a type and value
     *
     * @param type  type of the token
     * @param value value of the token
     */
    Token(final int type, final String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Get the value of the token
     *
     * @return value of the token
     */
    final String getValue() {
        return this.value;
    }

    /**
     * Get the type of the token
     *
     * @return type of the token
     */
    final int getType() {
        return this.type;
    }

    @Override
    public final String toString() {
        return this.type + ":" + this.value;
    }
}