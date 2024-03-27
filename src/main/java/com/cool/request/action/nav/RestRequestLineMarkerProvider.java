/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * RestRequestLineMarkerProvider.java is part of Cool Request
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

package com.cool.request.action.nav;

import com.cool.request.components.http.Controller;
import com.cool.request.common.icons.CoolRequestIcons;
import com.cool.request.common.state.SettingPersistentState;
import com.cool.request.lib.springmvc.ControllerAnnotation;
import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.cool.request.view.main.MainTopTreeView;
import com.cool.request.view.main.MainTopTreeViewManager;
import com.cool.request.view.tool.ProviderManager;
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
import java.util.List;

/**
 * 如果是一个合格的 http method，则在行号旁边添加一个按钮，点击后可以定位到导航栏，快速直接发起请求。
 *
 * @author zhangpj
 * @date 2024/01/17
 */
public class RestRequestLineMarkerProvider implements LineMarkerProvider {

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (!SettingPersistentState.getInstance().getState().addQuickSendButtonOnMethodLeft) return null;
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
     * 判断是否是一个合格的 spring mvc http method.
     *
     * @param targetPsiMethod
     * @return boolean
     */
    private boolean isRestControllerMethod(PsiMethod targetPsiMethod) {
        PsiClass psiClass = targetPsiMethod.getContainingClass();
        boolean isController = psiClass != null && AnnotationUtil.isAnnotated(psiClass, ControllerAnnotation.CONTROLLER.getAnnotationName(), 0);
        boolean isRestController = psiClass != null && AnnotationUtil.isAnnotated(psiClass, ControllerAnnotation.REST_CONTROLLER.getAnnotationName(), 0);
        //标记有@Controller和@RestController
        if (isController || isRestController) {
            //1.普通方法可以提取到http信息
            if (ParamUtils.hasHttpMethod(targetPsiMethod)) {
                return true;
            }
            //2.可能是接口定义的targetPsiMethod只是实现，没有被标记@GetMapper等的情况
            PsiMethod[] superMethods = targetPsiMethod.findSuperMethods(false);
            for (PsiMethod superMethod : superMethods) {
                if (ParamUtils.hasHttpMethod(superMethod)) {
                    return true;
                }
            }

            //3，如果都不行，在Main Tree中查找
            return hasInTreeView(targetPsiMethod);
        }
        //如果类是接口
        if (psiClass != null && psiClass.isInterface()) {
            if (ParamUtils.hasHttpMethod(targetPsiMethod)) {
                return true;
            }
        }
        return false;
    }


    private boolean hasInTreeView(PsiMethod method) {
        if (ProviderManager.getProvider(MainTopTreeViewManager.class, method.getProject()) == null) return false;
        return ProviderManager.findAndConsumerProvider(MainTopTreeViewManager.class, method.getProject(), mainTopTreeViewManager -> {
            for (List<MainTopTreeView.RequestMappingNode> value : mainTopTreeViewManager.getRequestMappingNodeMap().values()) {
                for (MainTopTreeView.RequestMappingNode requestMappingNode : value) {
                    Controller controller = requestMappingNode.getData();
                    for (PsiMethod ow : controller.getOwnerPsiMethod()) {
                        if (method == ow) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
    }

}
