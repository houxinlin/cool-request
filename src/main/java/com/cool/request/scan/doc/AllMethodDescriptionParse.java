/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * MethodDescriptionParse.java is part of Cool Request
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

package com.cool.request.scan.doc;


import com.cool.request.lib.springmvc.MethodDescription;
import com.cool.request.scan.MethodDescriptionParse;
import com.cool.request.scan.doc.swagger.SwaggerMethodDescriptionParse;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.List;

public class AllMethodDescriptionParse implements MethodDescriptionParse {
    private static final List<MethodDescriptionParse> all = List.of(new SwaggerMethodDescriptionParse(), new DefaultMethodDescriptionParse());

    private static AllMethodDescriptionParse parse = new AllMethodDescriptionParse();

    public static AllMethodDescriptionParse getInstance() {
        return parse;
    }

    @Override
    public MethodDescription parseMethodDescription(PsiMethod psiMethod) {
        for (MethodDescriptionParse methodDescriptionParse : all) {
            MethodDescription methodDescription = methodDescriptionParse.parseMethodDescription(psiMethod);
            if (methodDescription != null) return methodDescription;
        }
        throw new IllegalArgumentException("");
    }

    @Override
    public String parseParameterDescription(PsiParameter psiParameter, String defaultValue) {
        for (MethodDescriptionParse methodDescriptionParse : all) {
            String result = methodDescriptionParse.parseParameterDescription(psiParameter, defaultValue);
            if (result != null) return result;
        }
        throw new IllegalArgumentException("");
    }

    @Override
    public String parseParameterDescription(PsiParameter psiParameter) {
        return parseParameterDescription(psiParameter, "");
    }
}
