/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * SocketUtils.java is part of Cool Request
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

import com.cool.request.common.constant.CoolRequestConfigConstant;
import com.intellij.openapi.project.Project;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SocketUtils {
    private static final SocketUtils socketUtils = new SocketUtils();
    private static final Map<Project, Integer> portMap = new HashMap<>();

    private SocketUtils() {
    }

    public static SocketUtils getSocketUtils() {
        return socketUtils;
    }
    public static boolean canConnection(int port){
        return getSocketUtils().canUse(port);
    }
    /**
     * 获取项目的通信端口号
     *
     * @param project
     * @return
     */
    public synchronized int getPort(Project project) {
        Integer port = project.getUserData(CoolRequestConfigConstant.PortKey);
        if (port == null) {
            int newPort = generatorPort();
            project.putUserData(CoolRequestConfigConstant.PortKey, newPort);
            return newPort;
        }
        return port;

    }

    private boolean canUse(int port) {
        try (SocketChannel localhost = SocketChannel.open(new InetSocketAddress("localhost", port))) {
            return true;
        } catch (IOException ignored) {
        }
        return false;
    }

    private synchronized int generatorPort() {
        int port = 33333;
        boolean next = true;
        while (next) {
            //链接成功表示这个socket用不成
            if (!canUse(port)) return port;
            port++;
        }
        return port;
    }

    public static void writeStringToSocket(String msg, int port) throws IOException {
        try (SocketChannel projectSocket = SocketChannel.open(new InetSocketAddress("localhost", port))) {
            projectSocket.write(StandardCharsets.UTF_8.encode(msg));
        } catch (IOException e) {
            throw e;
        }
    }
}
