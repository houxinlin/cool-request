package com.hxl.plugin.springboot.invoke.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IPUtils {
    private static final List<String> FILTER = List.of("virtualbox", "docker");

    private static boolean filter(String name) {
        for (String item : FILTER) {
            if (name.toLowerCase().contains(item)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getAvailableIpAddresses() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (filter(networkInterface.getDisplayName())) continue;
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    // 过滤掉 IPv6 地址和回环地址
                    if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
                        ipList.add(address.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipList;
    }


}
