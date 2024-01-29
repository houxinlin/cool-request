package com.cool.request.utils;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {
    /**
     * 合并元素，并且重复元素优先第一个集合
     */
    public static <T> List<T> uniqueMerge(List<T> t1, List<T> t2) {
        if (t1 == null || t1.isEmpty()) {
            if (t2 != null) return t2;
            return new ArrayList<>();
        }
        if (t2 == null || t2.isEmpty()) {
            return t1;
        }

        List<T> result = new ArrayList<>(t1);
        for (T t : t2) {
            if (!result.contains(t)) result.add(t);
        }
        return result;
    }

    /**
     * 合并元素，即使元素相同
     */
    public static <T> List<T> merge(List<T> t1, List<T> t2) {
        if (t1 == null || t1.isEmpty()) {
            if (t2 != null) return t2;
            return new ArrayList<>();
        }
        if (t2 == null || t2.isEmpty()) {
            return t1;
        }

        List<T> result = new ArrayList<>(t1);
        result.addAll(t2);
        return result;
    }
}
