package com.swsandbox;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.utils.UUIDs;
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
    private static final CassandraClient client = new CassandraClient();
    private static final PreparedStatement insertStatement;
    public static final Logger logger = LoggerFactory.getLogger(WorkerServer.class);

    static
    {
        insertStatement = client.getSession().prepare("insert into events.events_raw (event_id, data) values (?, ?);");
    }

    public static void main(String[] args)
    {
        ZMQ.Context context = ZMQ.context(4);
        ZMQ.Socket socket = context.socket(ZMQ.PULL);
        socket.bind("tcp://*:5100");

        logger.info("worker server listening...");
        int c = 0;
        while (!Thread.interrupted())
        {
            c++;
            String msg = socket.recvStr();
//            logger.info("msg={}", msg);

            BoundStatement boundInsertStatement = new BoundStatement(insertStatement);
            boundInsertStatement.bind(UUIDs.timeBased(), msg);
            client.getSession().execute(boundInsertStatement);

//            logger.info("done with msg");

            if (c % 1000 == 0)
            {
                logger.info("1000 msgs received");
                c = 0;
            }
        }
    }
}
