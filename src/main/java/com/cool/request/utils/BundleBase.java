/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * BundleBase.java is part of Cool Request
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

package com.cool.request.utils;

import com.intellij.openapi.util.SystemInfoRt;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class BundleBase {
    public static final char MNEMONIC = 0x1B;

    public static @Nls String replaceMnemonicAmpersand(@Nullable @Nls String value) {
        if (value == null || value.indexOf('&') < 0 || value.indexOf(MNEMONIC) >= 0) {
            return value;
        }

        StringBuilder builder = new StringBuilder();
        boolean macMnemonic = value.contains("&&");
        boolean mnemonicAdded = false;
        int i = 0;
        while (i < value.length()) {
            char c = value.charAt(i);
            if (c == '\\') {
                if (i < value.length() - 1 && value.charAt(i + 1) == '&') {
                    builder.append('&');
                    i++;
                } else {
                    builder.append(c);
                }
            } else if (c == '&') {
                if (i < value.length() - 1 && value.charAt(i + 1) == '&') {
                    if (SystemInfoRt.isMac) {
                        if (!mnemonicAdded) {
                            mnemonicAdded = true;
                            builder.append(MNEMONIC);
                        }
                    }
                    i++;
                } else if (!SystemInfoRt.isMac || !macMnemonic) {
                    if (!mnemonicAdded) {
                        mnemonicAdded = true;
                        builder.append(MNEMONIC);
                    }
                }
            } else {
                builder.append(c);
            }
            i++;
        }
        String result = builder.toString();
        return result;
    }
}
