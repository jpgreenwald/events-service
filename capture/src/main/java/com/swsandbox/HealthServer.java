package com.swsandbox;

import com.swsandbox.util.Configuration;
import com.swsandbox.util.ConfigurationProperties;
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
        Configuration configuration = new Configuration(args);
        Server server = new Server();
        ServerConnector http = new ServerConnector(server);
        http.setPort(configuration.getInteger(ConfigurationProperties.http_port));
        http.setIdleTimeout(configuration.getInteger(ConfigurationProperties.http_idle_timeout));
        server.addConnector(http);
        server.setHandler(new HealthServer());
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