/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HttpMethodComboBox.java is part of Cool Request
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

package com.cool.request.view.main;

import com.cool.request.components.http.net.HttpMethod;
import com.cool.request.utils.HttpMethodIconUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangpengjun
 * @date 2024/1/12
 */
public class HttpMethodComboBox extends JComboBox<HttpMethod> {

    private final Map<HttpMethod, Icon> httpMethodIconMap;

    public HttpMethodComboBox() {
        // Initialize the icon map with icons for each HttpMethod
        httpMethodIconMap = new HashMap<>();
        Arrays.stream(HttpMethod.getValues()).forEach(method ->
                httpMethodIconMap.put(method, HttpMethodIconUtils.getIconByHttpMethod(method.name())));

        // Set the custom renderer
        setRenderer(new HttpMethodListCellRenderer());

        // Add HttpMethod values to the combo box
        for (HttpMethod method : HttpMethod.getValues()) {
            addItem(method);
        }
    }

    public HttpMethod getHTTPMethod() {
        Object selectedItem = getSelectedItem();
        if (selectedItem == null) return HttpMethod.GET;
        return HttpMethod.parse(selectedItem.toString());
    }

    // Custom ListCellRenderer to display icons
    private class HttpMethodListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            // Set text
            if (value instanceof HttpMethod) {
                label.setText((value).toString());
            }

            // Set icon
            if (value instanceof HttpMethod && httpMethodIconMap.containsKey(value)) {
                label.setIcon(httpMethodIconMap.get(value));
            }

            return label;
        }
    }

}
