package com.swsandbox;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
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
public class WorkerServer
{
    private static CassandraClient client;
    private static final PreparedStatement insertStatement;
    public static final Logger logger = LoggerFactory.getLogger(WorkerServer.class);

    static
    {
        insertStatement = client.getSession().prepare("insert into events.events_raw (event_id, data) values (?, ?);");
    }

    public static void main(String[] args)
    {
        Configuration configuration = new Configuration(args);
        client = new CassandraClient(configuration.getString(ConfigurationProperties.cassandra_cluster_host));
        ZMQ.Context context = ZMQ.context(configuration.getInteger(ConfigurationProperties.num_of_context_threads));
        ZMQ.Socket socket = context.socket(ZMQ.PULL);
        socket.bind(configuration.getString(ConfigurationProperties.worker_socket_host));

        logger.info("worker server listening...");
        int c = 0;
        while (!Thread.interrupted())
        {
            c++;
            String msg = socket.recvStr();
            BoundStatement boundInsertStatement = new BoundStatement(insertStatement);
            boundInsertStatement.bind(UUIDs.timeBased(), msg);
            client.getSession().execute(boundInsertStatement);
            if (c % 1000 == 0)
            {
                logger.info("1000 msgs received");
                c = 0;
            }
        }
    }
}
