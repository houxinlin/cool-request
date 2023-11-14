package com.hxl.plugin.springboot.invoke.utils;

import com.hxl.plugin.springboot.invoke.Constant;
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

    /**
     * 获取项目的通信端口号
     *
     * @param project
     * @return
     */
    public synchronized int getPort(Project project) {
        Integer port = project.getUserData(Constant.PortKey);
        if (port == null) {
            int newPort = generatorPort();
            project.putUserData(Constant.PortKey, newPort);
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
        try (SocketChannel projectSocket = SocketChannel.open(new InetSocketAddress("localhost", port));) {
            projectSocket.write(StandardCharsets.UTF_8.encode(msg));
        } catch (IOException e) {
            throw e;
        }
    }
}
