package com.swsandbox;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: jgreenwald
 * Date: 7/24/13
 * Time: 8:30 PM
 */

public class ServletServer extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(ServletServer.class);

    private static void setup(String[] args) throws Exception
    {
        Server server = new Server();
        ServerConnector http = new ServerConnector(server);
        http.setPort(8080);
        http.setIdleTimeout(3000);
        server.addConnector(http);
        ServletContextHandler sh = new ServletContextHandler();
        sh.addServlet(ServletServer.class, "/*");
        server.setHandler(sh);
        server.start();
        server.join();
    }

    public static void main(String[] args) throws Exception
    {
        setup(args);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.getWriter().write("hi");
        resp.getWriter().close();
    }
}
