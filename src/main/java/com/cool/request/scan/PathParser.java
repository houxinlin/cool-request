package com.cool.request.scan;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.List;

public interface PathParser {
    public List<String> parserPath(PsiClass originClass,PsiMethod psiMethod);
}
