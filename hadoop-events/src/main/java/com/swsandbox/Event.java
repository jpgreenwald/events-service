package com.swsandbox;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * User: jgreenwald
 * Date: 7/7/13
 * Time: 7:04 PM
 */
public class Event
{
    private String event;
    private UUID eventId;
    private Map<String, Object> data;

    public String getEvent()
    {
        return event;
    }

    public void setEvent(String event)
    {
        this.event = event;
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public void setData(Map<String, Object> data)
    {
        this.data = data;
    }

    public UUID getEventId()
    {
        return eventId;
    }

    public void setEventId(UUID eventId)
    {
        this.eventId = eventId;
    }

    @Override
    public String toString()
    {
        return "Event{" +
                "event='" + event + '\'' +
                ", eventId=" + eventId +
                ", data=" + data +
                '}';
    }
}
