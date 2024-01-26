package com.cool.request.component.http;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

public class TomcatServer implements StaticResourceServer {
    private final Tomcat tomcat = new Tomcat();

    private int port;
    private String root;

    public TomcatServer(int port, String root) {
        this.port = port;
        this.root = root;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    @Override
    public void start() {
        try {
            tomcat.setPort(port);
            tomcat.getConnector();
            tomcat.getHost();
            Context context = tomcat.addContext("/", this.root);
            tomcat.addServlet("/", "index", new DefaultServlet());
            context.addServletMappingDecoded("/", "index");
            tomcat.init();
            tomcat.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            tomcat.stop();
        } catch (LifecycleException e) {

        }
    }
}
