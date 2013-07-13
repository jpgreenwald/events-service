package com.swsandbox.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * User: jgreenwald
 * Date: 7/13/13
 * Time: 1:56 PM
 */
public class Configuration
{
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private Properties properties = new Properties();

    public Configuration()
    {
        this("./default.properties");
    }

    public Configuration(String[] args)
    {
        this((args.length > 0) ? args[0] : "./default.properties");
    }

    public Configuration(String path)
    {
        loadDefaults();
        FileInputStream fileInputStream;
        try
        {
            fileInputStream = new FileInputStream(path);
            properties.load(fileInputStream);
            fileInputStream.close();
        }
        catch (FileNotFoundException e)
        {
            logger.error("unable to find file: [{}], using default properties.", path);
        }
        catch (IOException e)
        {
            logger.error("unable to load fine: [{}], using default properties.", path);
        }
    }

    private void loadDefaults()
    {
        properties.setProperty("num_of_context_threads", "4");
        properties.setProperty("capture_socket_host", "tcp://localhost:5100");
        properties.setProperty("num_of_push_sockets", "4");
        properties.setProperty("http_port", "5050");
        properties.setProperty("http_idle_timeout", "3000");
        properties.setProperty("worker_socket_host", "tcp://*:5100");
        properties.setProperty("cassandra_cluster_host", "localhost");
        properties.setProperty("echo_count", "100000");
    }

    public String getString(ConfigurationProperties name)
    {
        return properties.getProperty(name.toString());
    }

    public Integer getInteger(ConfigurationProperties name)
    {
        return Integer.parseInt(properties.getProperty(name.toString()));
    }

    public Long getLong(ConfigurationProperties name)
    {
        return Long.parseLong(properties.getProperty(name.toString()));
    }
}
