/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * JavaClassComponent.java is part of Cool Request
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

package com.cool.request.components;

import com.cool.request.common.bean.components.Component;

/**
 * Java类中的某个组件，未来可能组件不是Java类中的数据，并且Java类可以导航到具体代码
 */
public interface JavaClassComponent extends CodeNavigation, Component {
    public String getJavaClassName();

    public String getUserProjectModuleName();

    public void setModuleName(String moduleName);

    public String getModuleName();

}
