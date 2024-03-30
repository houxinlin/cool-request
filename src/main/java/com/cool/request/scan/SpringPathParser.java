package com.cool.request.scan;

import com.cool.request.lib.springmvc.utils.ParamUtils;
import com.intellij.psi.PsiMethod;

import java.util.List;

public class SpringPathParser  implements PathParser{

    @Override
    public List<String> parserPath(PsiMethod psiMethod) {
        return ParamUtils.getHttpUrl(psiMethod);
    }
}
