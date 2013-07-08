package com.swsandbox;

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
import java.util.Random;

/**
 * User: jgreenwald
 * Date: 7/7/13
 * Time: 12:56 PM
 */
public class CaptureServer extends AbstractHandler
{
    private static final Logger logger = LoggerFactory.getLogger(CaptureServer.class);

    private static ZMQ.Context context = ZMQ.context(4);

    private ZMQ.Socket socket1;
    private ZMQ.Socket socket2;
    private ZMQ.Socket socket3;
    private ZMQ.Socket socket4;

    public CaptureServer()
    {
        socket1 = context.socket(ZMQ.PUSH);
        socket1.connect("tcp://localhost:5100");

        socket2 = context.socket(ZMQ.PUSH);
        socket2.connect("tcp://localhost:5100");

        socket3 = context.socket(ZMQ.PUSH);
        socket3.connect("tcp://localhost:5100");

        socket4 = context.socket(ZMQ.PUSH);
        socket4.connect("tcp://localhost:5100");

        logger.info("zmq connected and ready to push requests to localhost:5100, threadname: " + Thread.currentThread().getName());
    }

    public static void main(String[] args) throws Exception
    {
        context = ZMQ.context(4);
        logger.info("starting jetty server");
        Server server = new Server();

        // HTTP connector
        ServerConnector http = new ServerConnector(server);
        http.setHost("localhost");
        http.setPort(5050);
        http.setIdleTimeout(30000);

        // Set the connector
        server.addConnector(http);

        server.setHandler(new CaptureServer());
        server.start();
        server.join();
    }

    public static void close()
    {
        context.term();
        logger.info("zmq is shutting down");
    }


    public synchronized void sendMsg1(String msg)
    {
        socket1.send(msg);
    }

    public synchronized void sendMsg2(String msg)
    {
        socket2.send(msg);
    }

    public synchronized void sendMsg3(String msg)
    {
        socket3.send(msg);
    }

    public synchronized void sendMsg4(String msg)
    {
        socket4.send(msg);
    }

    public void sendMsg(String msg)
    {
        Random random = new Random();
        int i = random.nextInt(4) + 1;
        switch (i)
        {
            case 1:
                sendMsg1(msg);
                break;
            case 2:
                sendMsg2(msg);
                break;
            case 3:
                sendMsg3(msg);
                break;
            case 4:
                sendMsg4(msg);
                break;
            default:
                sendMsg4(msg);
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
