package com.swsandbox;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: jgreenwald
 * Date: 7/7/13
 * Time: 12:59 PM
 */
public class CassandraClient
{
    private static final Logger logger = LoggerFactory.getLogger(CassandraClient.class);

    private Cluster cluster;

    public CassandraClient()
    {
        connect("127.0.0.1");
    }

    public CassandraClient(String destination)
    {
        connect(destination);
    }

    public void connect(String node)
    {
        cluster = Cluster.builder().addContactPoint(node).build();
        Metadata metadata = cluster.getMetadata();
        logger.info("connected to cluster: {}", metadata.getClusterName());
        for (Host host : metadata.getAllHosts())
        {
            logger.info(" node info: datacenter: {}, host: {}, rack: {}", new Object[]{host.getDatacenter(), host.getAddress(), host.getRack()});
        }

    }

    public void close()
    {
        if (cluster != null)
        {
            cluster.shutdown();
        }
    }

    public Session getSession()
    {
        if (cluster != null)
        {
            return cluster.connect();
        }
        return null;
    }
}

