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
public class MetricsServer
{
    public static final Logger logger = LoggerFactory.getLogger(MetricsServer.class);

    public static void main(String[] args)
    {
        Configuration configuration = new Configuration(args);
        ZMQ.Context context = ZMQ.context(configuration.getInteger(ConfigurationProperties.num_of_context_threads));
        ZMQ.Socket socket = context.socket(ZMQ.PULL);
        socket.bind(configuration.getString(ConfigurationProperties.worker_socket_host));

        ZMQ.Socket metricSocket = context.socket(ZMQ.PUSH);
        metricSocket.connect("tcp://localhost:5200");


        logger.info("listening...and sending messages");
        int c = 0;
        while (!Thread.interrupted())
        {
            c++;
            String msg = socket.recvStr();

            String data = "{ \"type\":\"metric\",\"subType\":\"login_event\", \"data\":1 }";
            metricSocket.send(data);
            logger.info("data sent: " + data);

            if (c % configuration.getInteger(ConfigurationProperties.echo_count) == 0)
            {
                logger.info("{} messages received", configuration.getInteger(ConfigurationProperties.echo_count));
            }
        }
    }
}
