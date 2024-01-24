package com.cool.request.tool;

import com.cool.request.Constant;
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
        Map<Class<?>, Object> providerMap = project.getUserData(Constant.ProviderMapKey);
        if (providerMap == null) {
            providerMap = new HashMap<>();
            project.putUserData(Constant.ProviderMapKey, new HashMap<>());
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
        Map<Class<?>, Object> providerMap = project.getUserData(Constant.ProviderMapKey);
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
        Map<Class<?>, Object> providerMap = project.getUserData(Constant.ProviderMapKey);
        if (providerMap.containsKey(tClass)) {
            consumer.accept(((T) providerMap.get(tClass)));
            return true;
        }
        return false;
    }

    public static <T extends Provider, R> R findAndConsumerProvider(Class<T> tClass, Project project, Function<T, R> function) {
        Map<Class<?>, Object> providerMap = project.getUserData(Constant.ProviderMapKey);
        if (providerMap.containsKey(tClass)) {
            return function.apply(((T) providerMap.get(tClass)));
        }
        return null;
    }
}
