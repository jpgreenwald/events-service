package com.swsandbox;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import org.apache.cassandra.db.marshal.TimeUUIDType;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * User: jgreenwald
 * Date: 7/7/13
 * Time: 5:12 PM
 */
public class EventsReducer extends Reducer<Text, BytesWritable, Map<String, ByteBuffer>, List<ByteBuffer>>
{
    private CassandraClient cassandraClient = new CassandraClient();
    private PreparedStatement statement = cassandraClient.getSession().prepare("delete from events.events_raw where event_id = ?");

    @Override
    protected void reduce(Text key, Iterable<BytesWritable> values, Context context) throws IOException, InterruptedException
    {
        for (BytesWritable bytes : values)
        {
            Event event = JsonMapper.getMapper().readValue(bytes.getBytes(), Event.class);
            Map<String, ByteBuffer> keys = new LinkedHashMap<String, ByteBuffer>();
            keys.put("event_type", ByteBufferUtil.bytes(key.toString()));
            keys.put("event_id", ByteBufferUtil.bytes(event.getEventId()));
            List<ByteBuffer> data = new ArrayList<ByteBuffer>();
            data.add(ByteBuffer.wrap(JsonMapper.getMapper().writeValueAsBytes(event)));
            context.write(keys, data);

            //delete work
            BoundStatement boundStatement = new BoundStatement(statement);
            boundStatement.bind(event.getEventId());
            cassandraClient.getSession().execute(boundStatement);
        }
    }
}
