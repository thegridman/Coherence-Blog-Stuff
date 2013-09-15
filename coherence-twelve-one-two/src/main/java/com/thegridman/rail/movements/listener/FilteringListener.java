package com.thegridman.rail.movements.listener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.thegridman.rail.movements.MessageHeader;
import com.thegridman.rail.movements.TrainMovementActivationMessage;
import com.thegridman.rail.movements.TrainMovementMessage;
import com.thegridman.rail.movements.TrainMovementMessageId;
import net.ser1.stomp.Listener;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Jonathan Knight
 */
public class FilteringListener implements Listener
{
    private final Gson gson;
    private final Map<String,String> activeTrainIds;
    private final BufferedWriter writer;
    private final String newLine;
    private final DateFormat format;
    private int dotCount = 0;

    public FilteringListener(String fileName) throws Exception
    {
        activeTrainIds = new HashMap<String,String>();
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS");

        writer = new BufferedWriter(new FileWriter(fileName));
        newLine = System.getProperty("line.separator");

        JsonDeserializer<Long> longDeserializer = new JsonDeserializer<Long>() {
            public Long deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
            {
                if ("".equals(jsonElement.getAsString())) {
                    return 0L;
                }
                return jsonElement.getAsLong();
            }
        };

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Long.class, longDeserializer);
        builder.registerTypeAdapter(TrainMovementMessage.class, new TrainMovementMessage.JsonAdapter());
        builder.registerTypeAdapter(MessageHeader.class, new MessageHeader.JsonAdapter());

        gson = builder.create();
    }

    public void message(Map map, String json)
    {
        try
        {
            TrainMovementMessage[] messageArray = gson.fromJson(json, TrainMovementMessage[].class);
            for (TrainMovementMessage msg : messageArray)
            {
                TrainMovementMessageId id = msg.getId();
                if (msg instanceof TrainMovementActivationMessage)
                {
                    activeTrainIds.put(id.getTrainId(), ((TrainMovementActivationMessage) msg).getTrainUid());
                    writer.write(msg.getJsonText());
                    writer.write(newLine);
                    writer.flush();
                    System.err.println('\n' + format.format(new Date()) + " " + id.getTrainId() + " Acivated");
                }
                else if (activeTrainIds.containsKey(id.getTrainId()))
                {
                    writer.write(msg.getJsonText());
                    writer.write(newLine);
                    writer.flush();
                    System.err.println('\n' + format.format(new Date()) + " " + id.getTrainId() + " " + msg.getClass().getSimpleName());
                }
                else
                {
                    if (dotCount >= 100)
                    {
                        System.err.println(".");
                        dotCount = 0;
                    }
                    else
                    {
                        System.err.print(".");
                    }
                    dotCount++;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception
    {
        MovementProvider provider;
        provider = new NetworkRailMovementProvider();
        //provider = new MovementSimulator("/Users/jonathanknight/Projects/Coherence 12.1.2/data/movements.dat");

        //FilteringListener listener = new FilteringListener("/Users/jonathanknight/Projects/Coherence 12.1.2/data/filtered-movements.json");
        //provider.subscribe(listener);

        Thread.sleep(TimeUnit.HOURS.toMillis(24));

    }
}
