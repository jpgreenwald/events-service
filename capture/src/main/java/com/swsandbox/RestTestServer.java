package com.swsandbox;

import com.swsandbox.util.Configuration;
import com.swsandbox.util.ConfigurationProperties;
import com.swsandbox.util.SimplePool;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: jgreenwald
 * Date: 7/7/13
 * Time: 12:56 PM
 */
public class RestTestServer extends AbstractHandler
{


    private static void setup(String[] args) throws Exception
    {
        Server server = new Server();
        ServerConnector http = new ServerConnector(server);
        http.setPort(8080);
        http.setIdleTimeout(3000);
        server.addConnector(http);
        server.setHandler(new RestTestServer());
        server.start();
        server.join();
    }

    public static void main(String[] args) throws Exception
    {
        setup(args);
    }

    @Override
    public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("{ \"status\":\"still_alive\" }");
    }
}
