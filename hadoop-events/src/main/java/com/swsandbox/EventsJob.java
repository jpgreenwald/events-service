package com.swsandbox;

import org.apache.cassandra.hadoop.ConfigHelper;
import org.apache.cassandra.hadoop.cql3.CqlConfigHelper;
import org.apache.cassandra.hadoop.cql3.CqlOutputFormat;
import org.apache.cassandra.hadoop.cql3.CqlPagingInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * User: jgreenwald
 * Date: 7/7/13
 * Time: 5:07 PM
 */
public class EventsJob extends Configured implements Tool
{
    public static void main(String[] args) throws Exception
    {
        int res = ToolRunner.run(new Configuration(), new EventsJob(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] strings) throws Exception
    {
        Job job = new Job(getConf(), "com.swsandbox.EventsJob");
        job.setJarByClass(getClass());

        job.setMapperClass(EventsMapper.class);
        job.setReducerClass(EventsReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(BytesWritable.class);

        //output section
        job.setOutputFormatClass(CqlOutputFormat.class);
        //key space events and column family events
        ConfigHelper.setOutputColumnFamily(job.getConfiguration(), "events", "events");

        String query = "update events.events set data = ?";
        CqlConfigHelper.setOutputCql(job.getConfiguration(), query);
        ConfigHelper.setOutputInitialAddress(job.getConfiguration(), "localhost");
        ConfigHelper.setOutputPartitioner(job.getConfiguration(), "Murmur3Partitioner");

        //input section
        job.setInputFormatClass(CqlPagingInputFormat.class);
        ConfigHelper.setInputRpcPort(job.getConfiguration(), "9160");
        ConfigHelper.setInputInitialAddress(job.getConfiguration(), "localhost");
        ConfigHelper.setInputColumnFamily(job.getConfiguration(), "events", "events_raw");
        ConfigHelper.setInputPartitioner(job.getConfiguration(), "Murmur3Partitioner");
        CqlConfigHelper.setInputCQLPageRowSize(job.getConfiguration(), "3");

        boolean success = job.waitForCompletion(true);
        return success ? 0 : 1;
    }
}
