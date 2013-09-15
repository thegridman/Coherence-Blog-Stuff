package com.thegridman.rail.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.tangosol.util.Resources;
import com.thegridman.rail.movements.MessageHeader;
import com.thegridman.rail.movements.TrainMovementMessage;
import com.thegridman.rail.schedules.ScheduleEntry;
import com.thegridman.rail.schedules.ScheduleId;
import com.thegridman.rail.schedules.enums.STPIndicator;
import com.thegridman.rail.schedules.enums.TransactionType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonathan Knight
 */
public class RailGsonParser
{
    private final Gson gson;

    public RailGsonParser()
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Long.class, new LongAdapter());
        builder.registerTypeAdapter(TrainMovementMessage.class, new TrainMovementMessage.JsonAdapter());
        builder.registerTypeAdapter(ScheduleEntry.class, new ScheduleEntry.JsonAdapter());
        builder.registerTypeAdapter(ScheduleId.class, new ScheduleId.JsonAdapter());
        builder.registerTypeAdapter(MessageHeader.class, new MessageHeader.JsonAdapter());
        builder.registerTypeAdapter(STPIndicator.class, new STPIndicator.JsonAdapter());
        builder.registerTypeAdapter(TransactionType.class, new TransactionType.JsonAdapter());
        gson = builder.create();
    }

    public TrainMovementMessage parseTrainMovementMessage(String message)
    {
        return gson.fromJson(message, TrainMovementMessage.class);
    }

    public List<TrainMovementMessage> loadAllTrainMovementMessage(String fileName) throws IOException
    {
        List<TrainMovementMessage> messages = new ArrayList<>();
        URL movementURL = Resources.findFileOrResource(fileName, getClass().getClassLoader());
        BufferedReader reader = new BufferedReader(new InputStreamReader(movementURL.openStream()));
        String line = reader.readLine();
        while (line != null)
        {
            messages.add(parseTrainMovementMessage(line));
            line = reader.readLine();
        }
        return messages;
    }

    @SuppressWarnings("unchecked")
    public <T extends ScheduleEntry> T parseScheduleEntry(String message)
    {
        return (T) gson.fromJson(message, ScheduleEntry.class);
    }


    public static class LongAdapter implements JsonDeserializer<Long>
    {
        public Long deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            if ("".equals(jsonElement.getAsString())) {
                return 0L;
            }
            return jsonElement.getAsLong();
        }
    }

}
