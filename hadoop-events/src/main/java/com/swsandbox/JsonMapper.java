package com.swsandbox;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * User: jgreenwald
 * Date: 7/7/13
 * Time: 7:26 PM
 */
public class JsonMapper
{
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper getMapper()
    {
        return mapper;
    }
}
