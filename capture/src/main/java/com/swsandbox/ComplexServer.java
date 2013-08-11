package com.swsandbox;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * User: jgreenwald
 * Date: 7/24/13
 * Time: 8:30 PM
 */
@Singleton
@Path("/")
public class ComplexServer
{
    private static final Logger logger = LoggerFactory.getLogger(ComplexServer.class);

    private static void setup(String[] args) throws Exception
    {
        Server server = new Server();
        ServerConnector http = new ServerConnector(server);
        http.setPort(8080);
        http.setIdleTimeout(3000);
        server.addConnector(http);
        ServletContextHandler sh = new ServletContextHandler();
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages("com.swsandbox");
        sh.addServlet(new ServletHolder(new ServletContainer(resourceConfig)), "/*");
        server.setHandler(sh);
        server.start();
        server.join();
    }

    public static void main(String[] args) throws Exception
    {
        setup(args);
    }

    @GET
    public Response hi()
    {
        return Response.ok("yo").build();
    }
}
