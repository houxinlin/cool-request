package com.cool.request.action.nav;

import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.lib.springmvc.ControllerAnnotation;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 如果是一个合格的 http method，则在行号旁边添加一个按钮，点击后可以定位到导航栏，快速直接发起请求。
 *
 * @author zhangpj
 * @date 2024/01/17
 */
public class RestRequestLineMarkerProvider implements LineMarkerProvider {

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof PsiIdentifier && element.getParent() instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element.getParent();
            if (isRestControllerMethod(method)) {
                Icon icon = CoolRequestIcons.MAIN;
                return new LineMarkerInfo<>(
                        element,
                        element.getTextRange(),
                        icon,
                        psiElement -> "Send a request by clicking on cool request", // todo 国际化
                        new RestRequestNavHandler(),
                        GutterIconRenderer.Alignment.RIGHT);
            }
        }
        return null;
    }

    /**
     *  判断是否是一个合格的 spring mvc http method.
     *
     * @param method
     * @return boolean
     */
    private boolean isRestControllerMethod(PsiMethod method) {
        PsiClass psiClass = method.getContainingClass();
        boolean isController = psiClass != null && AnnotationUtil.isAnnotated(psiClass, ControllerAnnotation.Controller.getAnnotationName(), 0);
        boolean isRestController = psiClass != null && AnnotationUtil.isAnnotated(psiClass, ControllerAnnotation.RestController.getAnnotationName(), 0);
        if (isController || isRestController) {
            return ParamUtils.hasHttpMethod(method);
        }
        return false;
    }

}
