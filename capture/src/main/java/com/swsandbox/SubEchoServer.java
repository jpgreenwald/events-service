package com.swsandbox;

import com.swsandbox.util.Configuration;
import com.swsandbox.util.ConfigurationProperties;
import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: jgreenwald
 * Date: 7/7/13
 * Time: 1:27 PM
 */
public class SubEchoServer
{
    public static final Logger logger = LoggerFactory.getLogger(SubEchoServer.class);

    public static void main(String[] args)
    {
        Configuration configuration = new Configuration(args);
        ZMQ.Context context = ZMQ.context(configuration.getInteger(ConfigurationProperties.num_of_context_threads));
        ZMQ.Socket socket = context.socket(ZMQ.SUB);
        socket.connect("...some host");
        socket.subscribe("");
        logger.info("listening...");
        int c = 0;
        while (!Thread.interrupted())
        {
            c++;
            String msg = socket.recvStr();
            if (c % configuration.getInteger(ConfigurationProperties.echo_count) == 0)
            {
                logger.info("{} messages received", configuration.getInteger(ConfigurationProperties.echo_count));
            }
        }
    }
}
