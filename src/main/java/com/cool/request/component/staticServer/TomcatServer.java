package com.cool.request.component.staticServer;

import com.cool.request.utils.exception.StaticServerStartException;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

public class TomcatServer implements StaticResourceServer {

    private static final Logger LOG = Logger.getInstance(TomcatServer.class);
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
            tomcat.setBaseDir(System.getProperty("user.home") + "/.config/spring-invoke/invoke/"
                    + "/tomcat/" + staticServer.getPort());
            tomcat.getConnector();
            tomcat.getHost();
            Context context = tomcat.addContext("/", staticServer.getRoot());
            Wrapper defaultServlet = tomcat.addServlet(context, "index", new DefaultServlet());
            defaultServlet.addInitParameter("listings", "true");
            context.addServletMappingDecoded("/", "index");
            tomcat.init();
            tomcat.start();
        } catch (Exception e) {
            LOG.error(e);
            throw new StaticServerStartException(e);
        }
    }

    @Override
    public void stop() {
        try {
            tomcat.stop();
        } catch (Exception e) {

        }
        try {
            tomcat.destroy();
        } catch (Exception e) {

        }

    }
}
