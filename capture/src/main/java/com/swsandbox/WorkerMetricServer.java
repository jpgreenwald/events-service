package com.swsandbox;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.swsandbox.util.Configuration;
import com.swsandbox.util.ConfigurationProperties;
import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: jgreenwald
 * Date: 7/7/13
 * Time: 3:20 PM
 */
public class WorkerMetricServer
{
    private static CassandraClient client;
    private static PreparedStatement insertStatement;
    public static final Logger logger = LoggerFactory.getLogger(WorkerMetricServer.class);

    public static void main(String[] args)
    {
        Configuration configuration = new Configuration(args);
        client = new CassandraClient(configuration.getString(ConfigurationProperties.cassandra_cluster_host));
        insertStatement = client.getSession().prepare("insert into events.events_raw (event_id, data) values (?, ?);");
        ZMQ.Context context = ZMQ.context(configuration.getInteger(ConfigurationProperties.num_of_context_threads));
        ZMQ.Socket socket = context.socket(ZMQ.PULL);
        ZMQ.Socket pubSocket = context.socket(ZMQ.PUB);
        socket.bind(configuration.getString(ConfigurationProperties.worker_socket_host));
        pubSocket.bind("tcp://*:5300");
        logger.info("worker server listening...and bound to tcp://*:5300");
        int c = 0;
        Session session = client.getSession();
        while (!Thread.interrupted())
        {
            c++;
            String msg = socket.recvStr();
            BoundStatement boundInsertStatement = new BoundStatement(insertStatement);
            boundInsertStatement.bind(UUIDs.timeBased(), msg);
            session.execute(boundInsertStatement);
            pubSocket.send(msg);
            if (c % 1000 == 0)
            {
                logger.info("1000 msgs received");
                c = 0;
            }
        }
        pubSocket.close();
        session.shutdown();
    }
}
