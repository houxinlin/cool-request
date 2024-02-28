package com.cool.request.utils;

import com.cool.request.common.bean.components.controller.Controller;
import com.cool.request.common.bean.components.controller.CustomController;

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

    private static final String FOLDER_SEPARATOR = "/";

    private static final char FOLDER_SEPARATOR_CHAR = '/';

    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

    private static final String TOP_PATH = "..";

    private static final String CURRENT_PATH = ".";

    private static final char EXTENSION_SEPARATOR = '.';

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
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

    /**
     * Convert a {@code String} array into a comma delimited {@code String}
     * (i.e., CSV).
     * <p>Useful for {@code toString()} implementations.
     *
     * @param arr the array to display (potentially {@code null} or empty)
     * @return the delimited {@code String}
     */
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
        } catch (MalformedURLException e) {
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
        if (urlParts.length == 1) return urlParts[0];

        StringBuilder result = new StringBuilder();
        result.append(urlParts[0]);
        for (int i = 1; i < urlParts.length; i++) {
            String part = urlParts[i];
            if (!result.toString().endsWith("/") && !part.startsWith("/")) {
                result.append("/");
            }
            if (result.toString().endsWith("/") && part.startsWith("/")) {
                result.deleteCharAt(result.length() - 1);
            }
            result.append(part);
        }
        if (result.toString().startsWith("http")) return result.toString();
        if (result.toString().startsWith("/")) return result.toString();
        return "/" + result;
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
        } catch (Exception e) {
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
