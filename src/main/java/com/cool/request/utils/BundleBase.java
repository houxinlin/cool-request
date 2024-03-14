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
