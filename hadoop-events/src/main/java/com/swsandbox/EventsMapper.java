package com.swsandbox;

import org.apache.cassandra.db.marshal.TimeUUIDType;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

/**
 * User: jgreenwald
 * Date: 7/7/13
 * Time: 5:12 PM
 */
public class EventsMapper extends Mapper<Map<String, ByteBuffer>, Map<String, ByteBuffer>, Text, BytesWritable>
{
    @Override
    protected void map(Map<String, ByteBuffer> keys, Map<String, ByteBuffer> values, Context context) throws IOException, InterruptedException
    {
        UUID key = null;
        for (ByteBuffer s : keys.values())
        {
            key = TimeUUIDType.instance.compose(s);
            //there is only one key for this data
            break;
        }

        //these are skinny rows so there is only one data column for a given row
        String value = null;
        for (ByteBuffer s : values.values())
        {
            value = ByteBufferUtil.string(s);
        }


        Event event = JsonMapper.getMapper().readValue(value, Event.class);
        event.setEventId(key);
        context.write(new Text(event.getEvent()), new BytesWritable(JsonMapper.getMapper().writeValueAsBytes(event)));
    }


}
