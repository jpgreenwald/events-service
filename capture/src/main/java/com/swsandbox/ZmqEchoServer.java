package com.swsandbox;

import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: jgreenwald
 * Date: 7/7/13
 * Time: 1:27 PM
 */
public class ZmqEchoServer
{
    public static final Logger logger = LoggerFactory.getLogger(ZmqEchoServer.class);

    public static void main(String[] args)
    {
        ZMQ.Context context = ZMQ.context(5);
        ZMQ.Socket socket = context.socket(ZMQ.PULL);
        socket.bind("tcp://*:5100");

        logger.info("listening...");
        int c = 0;
        while (!Thread.interrupted())
        {
            c++;
            String msg = socket.recvStr();
            if (c % 1000 == 0)
            {
                logger.info("1000 msgs received");
            }
        }
    }
}
