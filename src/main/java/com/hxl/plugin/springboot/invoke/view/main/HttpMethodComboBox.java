package com.hxl.plugin.springboot.invoke.view.main;

import com.hxl.plugin.springboot.invoke.net.HttpMethod;
import com.hxl.plugin.springboot.invoke.utils.HttpMethodIconUtils;

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
