/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * Version.java is part of Cool Request
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

package com.cool.request.common.config;

public class Version implements Comparable<Version> {
    private String version;

    public Version(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(Version other) {
        String[] thisParts = this.version.split("\\.");
        String[] otherParts = other.version.split("\\.");

        int maxLength = Math.max(thisParts.length, otherParts.length);
        for (int i = 0; i < maxLength; i++) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int otherPart = i < otherParts.length ? Integer.parseInt(otherParts[i]) : 0;

            if (thisPart < otherPart) {
                return -1;
            }
            if (thisPart > otherPart) {
                return 1;
            }
        }
        return 0;
    }

}
