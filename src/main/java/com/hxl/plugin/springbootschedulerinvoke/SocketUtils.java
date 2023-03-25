package com.hxl.plugin.springbootschedulerinvoke;

import javax.net.ServerSocketFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class SocketUtils {
    public static int findPort() {
        int port = 33333;
        boolean find = true;
        while (find) {
            try {
                ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 1, InetAddress.getByName("localhost"));
                serverSocket.close();
                find = false;
            } catch (IOException e) {
                port++;
                find = true;
            }
        }

        return port;
    }
}
