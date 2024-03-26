package com.cool.request.view.main;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事件排序接口
 * <p>
 * 目前已知MainBottomHTTPResponseView需要放在最后面响应，因为脚本可能改变返回值
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HTTPEventOrder {
    public int MAX = 99999;
    public int MIN = 0;

    int value() default MIN;
}
