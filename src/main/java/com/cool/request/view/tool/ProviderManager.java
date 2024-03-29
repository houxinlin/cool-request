/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ProviderManager.java is part of Cool Request
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

package com.cool.request.view.tool;

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 关于项目相关数据得数据提供器和注册器
 * <p>
 * 用户不同组件内通信
 */
// TODO: 2024/1/20 所有数据向这个优化
public class ProviderManager {

    /**
     * 注册数据提供器
     *
     * @param targetClass 目标数据提供得类，这里不得使用value.getClass
     * @param key
     * @param value
     * @param project
     * @param <T>
     */
    public static <T extends Provider> void registerProvider(Class<T> targetClass, Key<T> key, T value, Project project) {
        project.putUserData(key, value);
        Map<Class<?>, Object> providerMap = project.getUserData(CoolRequestConfigConstant.ProviderMapKey);
        if (providerMap == null) {
            providerMap = new HashMap<>();
            project.putUserData(CoolRequestConfigConstant.ProviderMapKey, providerMap);
        }
        providerMap.put(targetClass, value);
    }

    /**
     * 获取数据提供器
     *
     * @param tClass  提供器得得类
     * @param project
     * @return
     */
    public static <T extends Provider> T getProvider(Class<T> tClass, Project project) {
        Map<Class<?>, Object> providerMap = project.getUserData(CoolRequestConfigConstant.ProviderMapKey);
        if (providerMap == null) return null;
        if (providerMap.containsKey(tClass)) {
            return ((T) providerMap.get(tClass));
        }
        return null;
    }

    /**
     * 查找并消费数据提供器 推荐使用这个
     *
     * @param tClass   提供器得得类
     * @param project
     * @param consumer
     * @param <T>
     * @return 返回是否消费
     */
    public static <T extends Provider> boolean findAndConsumerProvider(Class<T> tClass, Project project, Consumer<T> consumer) {
        Map<Class<?>, Object> providerMap = project.getUserData(CoolRequestConfigConstant.ProviderMapKey);
        if (providerMap.containsKey(tClass)) {
            consumer.accept(((T) providerMap.get(tClass)));
            return true;
        }
        return false;
    }

    public static <T extends Provider, R> R findAndConsumerProvider(Class<T> tClass, Project project, Function<T, R> function, R defaultValue) {
        Map<Class<?>, Object> providerMap = project.getUserData(CoolRequestConfigConstant.ProviderMapKey);
        if (providerMap.containsKey(tClass)) {
            return function.apply(((T) providerMap.get(tClass)));
        }
        return defaultValue;
    }

    public static <T extends Provider, R> R findAndConsumerProvider(Class<T> tClass, Project project, Function<T, R> function) {
        return findAndConsumerProvider(tClass, project, function, null);
    }
}
