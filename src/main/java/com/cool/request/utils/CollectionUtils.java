package com.cool.request.utils;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {
    public static <T> List<T> merge(List<T> t1, List<T> t2) {
        if (t1 == null || t1.isEmpty()) {
            if (t2 != null) return t2;
            return new ArrayList<>();
        }
        if (t2 == null || t2.isEmpty()) {
            if (t1 != null) return t1;
            return new ArrayList<>();
        }

        List<T> result = new ArrayList<>();
        for (T t : t1) {
            result.add(t);
        }
        for (T t : t2) {
            if (!result.contains(t)) result.add(t);
        }
        return result;
    }
}
