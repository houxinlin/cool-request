/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * ToolComponentPage.java is part of Cool Request
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

package com.cool.request.view;

/**
 * 所有工具组件需要实现这个接口,待定后续功能
 */
public interface ToolComponentPage {
    /**
     * 视图得id
     *
     * @return
     */
    public String getPageId();


    /**
     * 从其他页面跳转过来得时候会调用这个方法，附加数据
     *
     * @param object
     */
    public void attachViewData(Object object);
}
