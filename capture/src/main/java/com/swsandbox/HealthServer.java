package com.swsandbox;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: jgreenwald
 * Date: 7/10/13
 * Time: 4:32 PM
 */
public class HealthServer extends AbstractHandler
{

    public static void main(String[] args) throws Exception
    {
        Server server = new Server();
        // HTTP connector
        ServerConnector http = new ServerConnector(server);
        //http.setHost("localhost");
        http.setPort(5050);
        http.setIdleTimeout(30000);

        // Set the connector
        server.addConnector(http);

        server.setHandler(new CaptureServer());
        server.start();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().write("{ \"status\":\"ok\"}");
    }
}