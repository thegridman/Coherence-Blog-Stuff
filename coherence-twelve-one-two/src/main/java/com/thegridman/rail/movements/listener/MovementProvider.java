package com.thegridman.rail.movements.listener;

import net.ser1.stomp.Listener;

/**
 * @author Jonathan Knight
 */
public interface MovementProvider
{

    void subscribe(Listener listener) throws Exception;

    void close() throws Exception;

    void unsubscribe(Listener listener);
}
