package com.thegridman.rail.movements.listener;

import net.ser1.stomp.Listener;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Jonathan Knight
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
        //final BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/jonathanknight/Projects/Coherence 12.1.2/data/movements.dat"));
        final String newLine = System.getProperty("line.separator");

        Listener listener = new Listener()
        {
            private long count = 0;
            public void message(Map map, String s)
            {
//                try
//                {
//                    writer.write(s);
//                    writer.write(newLine);
//                    writer.flush();
                    System.out.println(String.valueOf(count++) + ": " + s);
//                }
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
            }
        };

        MovementProvider provider;
        //provider = new NetworkRailMovementProvider();
        provider = new MovementSimulator("/Users/jonathanknight/Projects/Coherence 12.1.2/data/filtered-movements.json");
        provider.subscribe(listener);

        Thread.sleep(TimeUnit.HOURS.toMillis(2));
//        writer.flush();
//        writer.close();
        provider.unsubscribe(listener);
        provider.close();
    }
}
