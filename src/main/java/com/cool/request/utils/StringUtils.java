package com.cool.request.utils;

import com.cool.request.common.bean.components.controller.Controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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

    public static String cleanPath(String path) {
        if (!hasLength(path)) {
            return path;
        }

        String normalizedPath = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
        String pathToUse = normalizedPath;

        // Shortcut if there is no work to do
        if (pathToUse.indexOf('.') == -1) {
            return pathToUse;
        }

        // Strip prefix from path to analyze, to not treat it as part of the
        // first path element. This is necessary to correctly parse paths like
        // "file:core/../core/io/Resource.class", where the ".." should just
        // strip the first "core" directory while keeping the "file:" prefix.
        int prefixIndex = pathToUse.indexOf(':');
        String prefix = "";
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (prefix.contains(FOLDER_SEPARATOR)) {
                prefix = "";
            } else {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            }
        }
        if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
            prefix = prefix + FOLDER_SEPARATOR;
            pathToUse = pathToUse.substring(1);
        }

        String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
        // we never require more elements than pathArray and in the common case the same number
        Deque<String> pathElements = new ArrayDeque<>(pathArray.length);
        int tops = 0;

        for (int i = pathArray.length - 1; i >= 0; i--) {
            String element = pathArray[i];
            if (CURRENT_PATH.equals(element)) {
                // Points to current directory - drop it.
            } else if (TOP_PATH.equals(element)) {
                // Registering top path found.
                tops++;
            } else {
                if (tops > 0) {
                    // Merging path element with element corresponding to top path.
                    tops--;
                } else {
                    // Normal path element found.
                    pathElements.addFirst(element);
                }
            }
        }

        // All path elements stayed the same - shortcut
        if (pathArray.length == pathElements.size()) {
            return normalizedPath;
        }
        // Remaining top paths need to be retained.
        for (int i = 0; i < tops; i++) {
            pathElements.addFirst(TOP_PATH);
        }
        // If nothing else left, at least explicitly point to current path.
        if (pathElements.size() == 1 && pathElements.getLast().isEmpty() && !prefix.endsWith(FOLDER_SEPARATOR)) {
            pathElements.addFirst(CURRENT_PATH);
        }

        final String joined = collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
        // avoid string concatenation with empty prefix
        return prefix.isEmpty() ? joined : prefix + joined;
    }

    public static String collectionToDelimitedString(Collection<?> coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }

    public static String collectionToCommaDelimitedString(Collection<?> coll) {
        return collectionToDelimitedString(coll, ",");
    }

    public static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix) {

        if (CollectionUtils.isEmpty(coll)) {
            return "";
        }

        int totalLength = coll.size() * (prefix.length() + suffix.length()) + (coll.size() - 1) * delim.length();
        for (Object element : coll) {
            totalLength += String.valueOf(element).length();
        }

        StringBuilder sb = new StringBuilder(totalLength);
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    public static String[] delimitedListToStringArray(String str, String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }

    public static String[] delimitedListToStringArray(
            String str, String delimiter, String charsToDelete) {

        if (str == null) {
            return EMPTY_STRING_ARRAY;
        }
        if (delimiter == null) {
            return new String[]{str};
        }

        List<String> result = new ArrayList<>();
        if (delimiter.isEmpty()) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        } else {
            int pos = 0;
            int delPos;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                pos = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                // Add rest of String, but not in case of empty input.
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }

    public static String deleteAny(String inString, String charsToDelete) {
        if (!hasLength(inString) || !hasLength(charsToDelete)) {
            return inString;
        }

        int lastCharIndex = 0;
        char[] result = new char[inString.length()];
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                result[lastCharIndex++] = c;
            }
        }
        if (lastCharIndex == inString.length()) {
            return inString;
        }
        return new String(result, 0, lastCharIndex);
    }

    public static String[] toStringArray(Collection<String> collection) {
        return (!CollectionUtils.isEmpty(collection) ? collection.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY);
    }

    /**
     * Copy the given {@link Enumeration} into a {@code String} array.
     * <p>The {@code Enumeration} must contain {@code String} elements only.
     *
     * @param enumeration the {@code Enumeration} to copy
     *                    (potentially {@code null} or empty)
     * @return the resulting {@code String} array
     */
    public static String[] toStringArray(Enumeration<String> enumeration) {
        return (enumeration != null ? toStringArray(Collections.list(enumeration)) : EMPTY_STRING_ARRAY);
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
            if (StringUtils.isEmpty(part)) continue;
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

    public static String getFullUrl(Controller requestMappingModel) {
        String url = requestMappingModel.getUrl();
        if (StringUtils.isEmpty(url)) return requestMappingModel.getContextPath();
        if (!url.startsWith("/")) url = "/" + url;
        return joinUrlPath(requestMappingModel.getContextPath(), url);
    }

    /**
     * 移除主机部分，导出到第三方平台的时候可能不需要主机部分
     *
     * @param url 原url
     * @return 路径
     */
    public static String removeHostFromUrl(String url) {
        if (StringUtils.isEmpty(url)) return "";
        try {
            URI uri = new URI(url);
            URI newUri = new URI(
                    null,
                    null,
                    null,
                    0,
                    uri.getPath(),
                    uri.getQuery(),
                    uri.getFragment()
            );

            return newUri.toString();
        } catch (URISyntaxException ignored) {
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
