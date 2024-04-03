/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * StringUtils.java is part of Cool Request
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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final String[] EMPTY_STRING_ARRAY = {};
    private static final Pattern TRIM_PATTERN = Pattern.compile("^/*(.*?)/*$");

    private static final String FOLDER_SEPARATOR = "/";

    private static final char FOLDER_SEPARATOR_CHAR = '/';

    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

    private static final String TOP_PATH = "..";

    private static final String CURRENT_PATH = ".";

    private static final char EXTENSION_SEPARATOR = '.';

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }
    public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        if (index + substring.length() > str.length()) {
            return false;
        }
        for (int i = 0; i < substring.length(); i++) {
            if (str.charAt(index + i) != substring.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    public static String formatBytes(long bytes) {
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double result = bytes;

        while (result >= 1024 && unitIndex < units.length - 1) {
            result /= 1024;
            unitIndex++;
        }

        return String.format("%.2f%s", result, units[unitIndex]);
    }

    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        if (str == null) {
            return EMPTY_STRING_ARRAY;
        }

        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String replace(String inString, String oldPattern, String newPattern) {
        if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
            return inString;
        }
        int index = inString.indexOf(oldPattern);
        if (index == -1) {
            // no occurrence -> can return input as-is
            return inString;
        }

        int capacity = inString.length();
        if (newPattern.length() > oldPattern.length()) {
            capacity += 16;
        }
        StringBuilder sb = new StringBuilder(capacity);

        int pos = 0;  // our position in the old string
        int patLen = oldPattern.length();
        while (index >= 0) {
            sb.append(inString, pos, index);
            sb.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }

        // append any characters to the right of a match
        sb.append(inString, pos, inString.length());
        return sb.toString();
    }

    public static String[] toStringArray(Collection<String> collection) {
        return (!CollectionUtils.isEmpty(collection) ? collection.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY);
    }


    public static String arrayToDelimitedString(Object[] arr, String delim) {
        if (ObjectUtils.isEmpty(arr)) {
            return "";
        }
        if (arr.length == 1) {
            return ObjectUtils.nullSafeToString(arr[0]);
        }

        StringJoiner sj = new StringJoiner(delim);
        for (Object elem : arr) {
            sj.add(String.valueOf(elem));
        }
        return sj.toString();
    }

    public static String arrayToCommaDelimitedString(Object[] arr) {
        return arrayToDelimitedString(arr, ",");
    }


    public static boolean hasText(String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String joinSingleQuotation(String str) {
        return "'" + str + "'";
    }

    public static boolean hasLength(String str) {
        return (str != null && !str.isEmpty());
    }

    /**
     * 文件路径转URL
     */
    public static URL fileToURL(String str) {
        try {
            return new File(str).toURI().toURL();
        } catch (MalformedURLException e) {

        }
        return null;
    }

    /**
     * 判断是否是url
     *
     * @param str 目前字符
     * @return
     */
    public static boolean isUrl(String str) {
        try {
            new URL(str);
            return true;
        } catch (MalformedURLException ignored) {
        }
        return false;
    }

    public static boolean isStartWithIgnoreSpace(String text, String str) {
        if (isEmpty(text) || isEmpty(str)) return false;
        return text.toLowerCase().trim().startsWith(str.toLowerCase());
    }

    /**
     * 检测两个字符是否相等
     */
    public static boolean isEquals(String src, String other) {
        if (src != null) return src.equals(other);
        return other == null;
    }

    public static boolean isEqualsIgnoreCase(String src, String other) {
        if (src != null) return src.equalsIgnoreCase(other);
        return other == null;
    }

    public static boolean isValidJson(String jsonString) {
        String jsonPattern = "\\{.*\\}|\\[.*\\]";
        Pattern pattern = Pattern.compile(jsonPattern);
        Matcher matcher = pattern.matcher(jsonString);
        return matcher.matches();
    }

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    public static String addPrefixIfMiss(String src, String prefix) {
        if (src.startsWith(prefix)) return src;
        return prefix + src;
    }

    public static String headerNormalized(String headerName) {
        if (headerName == null || headerName.isEmpty()) {
            return headerName;
        }
        StringBuilder normalizedHeader = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : headerName.toLowerCase().toCharArray()) {
            if (c == '-') {
                normalizedHeader.append("-");
                capitalizeNext = true;
                continue;
            }
            normalizedHeader.append(capitalizeNext ? Character.toUpperCase(c) : c);
            if (capitalizeNext) {
                capitalizeNext = false;
            }
        }
        return normalizedHeader.toString();
    }

    public static String joinUrlPath(String... urlParts) {
        return collectPath(urlParts);
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen = length(cs);
        if (strLen != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static String collectPath(String... pathParts) {
        StringBuilder sb = new StringBuilder();
        for (String item : pathParts) {
            if (!isBlank(item)) {
                String path = trimPath(item);
                if (isNotBlank(path)) {
                    if (path.startsWith("http")){
                        sb.append(path);
                    }else {
                        sb.append('/').append(path);
                    }
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : String.valueOf('/');
    }

    private static String trimPath(String value) {
        Matcher matcher = TRIM_PATTERN.matcher(value);
        return matcher.find() && org.apache.commons.lang3.StringUtils.isNotBlank(matcher.group(1)) ? matcher.group(1) : null;
    }


    /**
     * 移除主机部分，导出到第三方平台的时候可能不需要主机部分
     *
     * @param url 原url
     * @return 路径
     */
    public static String removeHostFromUrl(String url) {
        if (StringUtils.isEmpty(url)) return "";
        if (!url.startsWith("http")) return url;
        try {
            StringBuilder stringBuilder = new StringBuilder();
            URL urlObj = new URL(url);
            if (urlObj.getPath() != null) {
                stringBuilder.append(urlObj.getPath());
            }
            if (urlObj.getQuery() != null) {
                stringBuilder.append("?");
                stringBuilder.append(urlObj.getQuery());
            }
            return stringBuilder.toString();
        } catch (Exception ignored) {
        }
        return url;
    }

    public static String calculateMD5(String input) {
        try {
            // 获取MD5算法实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] byteData = input.getBytes();
            md.update(byteData);
            byte[] mdBytes = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte mdByte : mdBytes) {
                String hex = Integer.toHexString(0xff & mdByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
