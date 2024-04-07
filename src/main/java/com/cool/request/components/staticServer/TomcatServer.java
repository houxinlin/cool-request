/*
 * Copyright 2024 XIN LIN HOU<hxl49508@gmail.com>
 * TomcatServer.java is part of Cool Request
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

package com.cool.request.components.staticServer;

import com.cool.request.utils.exception.StaticServerStartException;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.valves.ErrorReportValve;

public class TomcatServer implements StaticResourceServer {

    private static final Logger LOG = Logger.getInstance(TomcatServer.class);
    private final Tomcat tomcat = new Tomcat();
    private final StaticServer staticServer;
    private final CoolServlet coolServlet = new CoolServlet();

    private static class CoolServlet extends DefaultServlet {
        public void setListDir(boolean listDir) {
            this.listings = listDir;
        }
    }

    @Override
    public void setListDir(boolean listDir) {
        coolServlet.setListDir(listDir);
    }

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
            // 容器
            Context context = tomcat.addContext("/", staticServer.getRoot());
            Wrapper defaultServlet = tomcat.addServlet(context, "index", coolServlet);
            if (staticServer.isListDir()) {
                defaultServlet.addInitParameter("listings", "true");
                defaultServlet.addInitParameter("fileEncoding", "utf-8");
            }
            defaultServlet.addInitParameter("showServerInfo", "false");
            context.addServletMappingDecoded("/", "index");
            // 禁用默认的错误页面中的服务器信息
            ErrorReportValve errorValve = new ErrorReportValve();
            errorValve.setShowServerInfo(false);
            context.getPipeline().addValve(errorValve);
            // 启动
            tomcat.getConnector();
            tomcat.getHost();
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
        } catch (Exception ignored) {

        }
        try {
            tomcat.destroy();
        } catch (Exception ignored) {

        }

    }
}
