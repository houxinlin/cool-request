/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SwaggerMethodDecriptionParse.java is part of Cool Request
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

package com.cool.request.scan.swagger;

import com.cool.request.lib.springmvc.MethodDescription;
import com.cool.request.lib.springmvc.ParameterAnnotationDescriptionUtils;
import com.cool.request.scan.MethodDescriptionParse;
import com.intellij.psi.PsiMethod;

public class SwaggerMethodDescriptionParse implements MethodDescriptionParse {

    @Override
    public MethodDescription parse(PsiMethod psiMethod) {
        return ParameterAnnotationDescriptionUtils.getMethodDescription(psiMethod);
    }
}
