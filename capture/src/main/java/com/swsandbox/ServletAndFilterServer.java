package com.swsandbox;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;

/**
 * User: jgreenwald
 * Date: 7/24/13
 * Time: 8:30 PM
 */

public class ServletAndFilterServer extends HttpServlet implements Filter
{
    private static final Logger logger = LoggerFactory.getLogger(ServletAndFilterServer.class);

    private static void setup(String[] args) throws Exception
    {
        Server server = new Server();
        ServerConnector http = new ServerConnector(server);
        http.setPort(8080);
        http.setIdleTimeout(3000);
        server.addConnector(http);
        ServletContextHandler sh = new ServletContextHandler();
        sh.addFilter(ServletAndFilterServer.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        sh.addServlet(ServletAndFilterServer.class, "/*");
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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        System.out.println("I am init the filter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        //System.out.println("I am doFilter");
        chain.doFilter(request, response);
    }
}
