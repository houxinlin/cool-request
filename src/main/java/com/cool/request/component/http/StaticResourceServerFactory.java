package com.cool.request.component.http;

/**
 * 创建静态服务器
 */
public class StaticResourceServerFactory {
    public static StaticResourceServer createStaticResourceServer(int port,String bastPath){
        return  new TomcatServer(port,bastPath);
    }
}
