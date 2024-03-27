/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * HTTPResponseStatusPanel.java is part of Cool Request
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

import com.cool.request.components.http.net.HTTPResponseBody;
import com.cool.request.components.http.net.RequestContext;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.TimerUtils;

import javax.swing.*;
import java.awt.*;

public class HTTPResponseStatusPanel {
    private JLabel statusValue;
    private JLabel timerValue;
    private JLabel sizeLabel;
    private JPanel root;

    private Color getColor(int code) {
        return code == 200 ? Color.decode("#1A9F6C") : Color.decode("#E74C3C");
    }

    public void setStatus(int value) {
        statusValue.setText(value + "");
        statusValue.setForeground(getColor(value));
        timerValue.setForeground(getColor(value));
        sizeLabel.setForeground(getColor(value));
    }

    public void parse(HTTPResponseBody httpResponseBody, RequestContext requestContext) {
        setSize(httpResponseBody.getSize());
        setTimer(System.currentTimeMillis() - requestContext.getBeginTimeMillis());
        setStatus(httpResponseBody.getCode());
        root.setVisible(true);
    }

    public void setSize(long value) {
        sizeLabel.setText(StringUtils.formatBytes(value));
    }

    public void setTimer(long value) {
        timerValue.setText(TimerUtils.convertMilliseconds(value));
    }

    public JPanel getRoot() {
        return root;
    }
}
