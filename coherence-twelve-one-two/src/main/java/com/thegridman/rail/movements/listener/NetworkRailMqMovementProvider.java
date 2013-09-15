package com.thegridman.rail.movements.listener;

import com.tangosol.util.Base;
import net.ser1.stomp.Client;
import net.ser1.stomp.Listener;

import java.util.Map;

/**
 * @author Jonathan Knight
 */
public class NetworkRailMqMovementProvider implements MovementProvider, Listener
{
    public static final String MOVEMENT_TOPIC_NAME = "/topic/TRAIN_MVT_ALL_TOC";

    private final Client client;

    public NetworkRailMqMovementProvider()
    {
        try
        {
            client = new Client("datafeeds.networkrail.co.uk", 61618, "jk@thegridman.com", "JK12345jk@");
            client.addErrorListener(this);
        }
        catch (Exception e)
        {
            throw Base.ensureRuntimeException(e);
        }
    }

    public void subscribe(Listener listener) throws Exception
    {
        client.subscribe(MOVEMENT_TOPIC_NAME, listener);
    }

    public void unsubscribe(Listener listener)
    {
        client.unsubscribe(MOVEMENT_TOPIC_NAME, listener);
    }

    public void close()
    {
        client.disconnect();
    }

    public void message(Map map, String s)
    {
        System.err.println("ERROR: " + map + s);
    }
}
