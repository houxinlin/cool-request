package com.cool.request.component.http;

/**
 * 未来有时间可以加入其他服务器，目前只支持Tomcat
 */
public interface StaticResourceServer {
    void start();

    void stop();

}
