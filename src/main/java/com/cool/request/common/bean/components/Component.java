/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * Component.java is part of Cool Request
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

package com.cool.request.common.bean.components;

import com.cool.request.components.ComponentType;

/**
 * 基本组件
 */
public interface Component {
    String getId();

    /**
     * 着两个方法用来标记组件的可用状态，当主动/被动刷新数据时，会先将所有已有组件设置为不可用，新数据被添加时
     * 如果已经存在，则标记为不可用
     * 最后，删除不可用的组件，已达到刷新效果
     */
    public void setAvailable(boolean isAvailable);

    public boolean isAvailable();

    public void setId(String id);

    public ComponentType getComponentType();
}
