/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * NavigationUtils.java is part of Cool Request
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

package com.cool.request.utils;

import com.cool.request.components.scheduled.BasicScheduled;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;

import static com.cool.request.utils.PsiUtils.*;

/**
 * 导航栏工具类
 *
 * @author zhangpj
 * @date 2024/01/17
 */
public class NavigationUtils {
    public static PsiMethod findClickedMethod(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null) {
            return null;
        }
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            Caret caret = editor.getCaretModel().getPrimaryCaret();
            int offset = caret.getOffset();
            PsiElement elementAtCaret = psiFile.findElementAt(offset);
            if (elementAtCaret instanceof PsiMethod) {
                return (PsiMethod) elementAtCaret;
            } else {
                return PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethod.class, false);
            }
        }
        return null;
    }

    public static void jumpToCode(Project project, String className, String method) {
        PsiClass targetPsiClass = findClassByName(project, className);
        if (targetPsiClass == null) return;
        List<PsiMethod> methodInClass = findMethodInClass(targetPsiClass, method);
        if (methodInClass.isEmpty()) return;
        methodInClass.get(0).navigate(true);
    }

    public static void jumpToSpringScheduledMethod(Project project, BasicScheduled springScheduled) {
        PsiClass psiClass = findClassByName(project, springScheduled.getModuleName(), springScheduled.getClassName());
        if (psiClass != null) {
            PsiMethod methodInClass = findMethodInClassOne(psiClass, springScheduled.getMethodName());
            if (methodInClass != null) PsiUtils.methodNavigate(methodInClass);
        }
    }
}
