package com.thegridman.rail.movements.listener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.thegridman.rail.movements.MessageHeader;
import com.thegridman.rail.movements.TrainMovementMessage;
import com.thegridman.rail.movements.TrainMovementMovementMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jonathan Knight
 */
public class FilteredFileProcessor
{
    private final Gson gson;
    private final BufferedReader reader;

    public FilteredFileProcessor(String fileName) throws Exception
    {
        reader = new BufferedReader(new FileReader(fileName));

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
        //builder.registerTypeAdapter(TiplocData.class, new TiplocData.JsonAdapter());
        builder.registerTypeAdapter(TrainMovementMessage.class, new TrainMovementMessage.JsonAdapter());
        builder.registerTypeAdapter(MessageHeader.class, new MessageHeader.JsonAdapter());

        gson = builder.create();
    }

    public void run() throws Exception
    {
        List<String> trainIdList = Arrays.asList("325A24MO02","771L65MN02","162C37MO02","782F56MQ02","515D45MS02","541N19MN02");
        List<String> singleTrainData = new ArrayList<>();
        Map<String,TrainMovementMessage> lastMessages = new HashMap<>();
        Map<String,Integer> counts = new HashMap<>();
        String line = reader.readLine();
        while (line != null)
        {
            TrainMovementMessage message = gson.fromJson(line, TrainMovementMessage.class);
            String trainId = message.getId().getTrainId();
            lastMessages.put(trainId, message);
            if (counts.containsKey(trainId))
            {
                Integer count = counts.get(trainId);
                counts.put(trainId, count + 1);
            }
            else
            {
                counts.put(trainId, 1);
            }
            if (trainIdList.contains(trainId))
            {
                singleTrainData.add(line);
            }
            line = reader.readLine();
        }

        int count = 0;
        for (TrainMovementMessage message : lastMessages.values())
        {
            if (message instanceof TrainMovementMovementMessage && ((TrainMovementMovementMessage) message).getPlannedEventType().equals("DESTINATION"))
            {
                String trainId = message.getId().getTrainId();
                System.out.println("" + ++count + " " + message.getId() + " " + ((TrainMovementMovementMessage) message).getPlannedEventType() + " " + counts.get(trainId));
            }
        }

        System.out.println("---------");
        for (String l : singleTrainData)
        {
            System.out.println(l);
        }
        System.out.println("---------");
    }

    public static void main(String[] args) throws Exception
    {
        FilteredFileProcessor processor = new FilteredFileProcessor("/Users/jonathanknight/Projects/Coherence 12.1.2/data/filtered-movements.json");
        processor.run();
    }
}
