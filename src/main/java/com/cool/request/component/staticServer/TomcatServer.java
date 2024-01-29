package com.cool.request.component.staticServer;

import com.cool.request.utils.exception.StaticServerStartException;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

public class TomcatServer implements StaticResourceServer {
    private final Tomcat tomcat = new Tomcat();
    private StaticServer staticServer;


    public TomcatServer(StaticServer staticServer) {
        this.staticServer = staticServer;
    }

    @Override
    public String getId() {
        return staticServer.getId();
    }

    @Override
    public void start() {
        try {
            tomcat.setPort(staticServer.getPort());
            tomcat.getConnector();
            tomcat.getHost();
            Context context = tomcat.addContext("/", staticServer.getRoot());
            tomcat.addServlet("/", "index", new DefaultServlet());
            context.addServletMappingDecoded("/", "index");
            tomcat.init();
            tomcat.start();
        } catch (Exception e) {
            throw new StaticServerStartException();
        }
    }

    @Override
    public void stop() {
        try {
            tomcat.stop();
        } catch (LifecycleException e) {

        }
        try {
            tomcat.destroy();
        } catch (LifecycleException e) {

        }

    }
}
