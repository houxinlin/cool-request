/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SpringScheduledSpringInvokeEndpointWrapper.java is part of Cool Request
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

package com.cool.request.utils;

import com.cool.request.common.model.SpringScheduledSpringInvokeEndpoint;

public class SpringScheduledSpringInvokeEndpointWrapper {
    private SpringScheduledSpringInvokeEndpoint springScheduledSpringInvokeEndpoint;
    private int port;

    public SpringScheduledSpringInvokeEndpointWrapper(SpringScheduledSpringInvokeEndpoint springScheduledSpringInvokeEndpoint, int port) {
        this.springScheduledSpringInvokeEndpoint = springScheduledSpringInvokeEndpoint;
        this.port = port;
    }

    public SpringScheduledSpringInvokeEndpoint getSpringScheduledSpringInvokeEndpoint() {
        return springScheduledSpringInvokeEndpoint;
    }

    public void setSpringScheduledSpringInvokeEndpoint(SpringScheduledSpringInvokeEndpoint springScheduledSpringInvokeEndpoint) {
        this.springScheduledSpringInvokeEndpoint = springScheduledSpringInvokeEndpoint;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
