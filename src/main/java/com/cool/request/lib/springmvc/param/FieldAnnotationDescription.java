package com.cool.request.lib.springmvc.param;

import com.intellij.psi.PsiField;

public interface FieldAnnotationDescription {
    /**
     * 获取序列化的真实名字
     */
    public String getRelaName(PsiField field);
}
