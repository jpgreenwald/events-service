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
import java.util.Random;

/**
 * User: jgreenwald
 * Date: 7/7/13
 * Time: 12:56 PM
 */
public class PooledCaptureServer extends AbstractHandler
{
    private static final Logger logger = LoggerFactory.getLogger(PooledCaptureServer.class);
    private static Configuration configuration;
    private static ZMQ.Context context;
    private static SimplePool<ZMQ.Socket> pool;

    private static void setup(String[] args) throws Exception
    {
        configuration = new Configuration(args);
        context = ZMQ.context(configuration.getInteger(ConfigurationProperties.num_of_context_threads));
        Collection<ZMQ.Socket> collection = new ArrayList<>();
        for (int i = 0; i < configuration.getInteger(ConfigurationProperties.num_of_push_sockets); i++)
        {
            ZMQ.Socket socket = context.socket(ZMQ.PUSH);
            socket.connect(configuration.getString(ConfigurationProperties.capture_socket_host));
            collection.add(socket);
        }
        pool = new SimplePool<>(collection);

        Server server = new Server();
        ServerConnector http = new ServerConnector(server);
        http.setPort(configuration.getInteger(ConfigurationProperties.http_port));
        http.setIdleTimeout(configuration.getInteger(ConfigurationProperties.http_idle_timeout));
        server.addConnector(http);
        server.setHandler(new PooledCaptureServer());
        server.start();
        server.join();
    }

    public static void main(String[] args) throws Exception
    {
        setup(args);
    }

    public static void close()
    {
        context.term();
        logger.info("zmq is shutting down");
    }

    public void sendMsg(String msg)
    {
        try
        {
            ZMQ.Socket socket = pool.take();
            socket.send(msg);
            pool.put(socket);
        }
        catch (InterruptedException e)
        {
            logger.error("Unable to send msg: {} due to error: {}", msg, e);
        }
    }

    @Override
    public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        InputStream is = request.getInputStream();
        byte[] buf = new byte[4096];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while (is.read(buf) != -1)
        {
            outputStream.write(buf);
        }
        String msg = outputStream.toString().trim();
        outputStream.close();
        is.close();
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        if (!msg.equals("no_send"))
        {
            sendMsg(msg);
            response.getWriter().println("{ \"status\":\"ok\" }");
        }
        else
        {
            response.getWriter().println("{ \"status\":\"still_alive\" }");
        }
    }
}
