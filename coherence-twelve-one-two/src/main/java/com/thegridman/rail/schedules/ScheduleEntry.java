package com.thegridman.rail.schedules;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * @author Jonathan Knight
 */
public interface ScheduleEntry
{

    /**
     * JSON deserializer for ScheduleEntry classes
     */
    public static class JsonAdapter implements JsonDeserializer<ScheduleEntry>
    {
        @Override
        public ScheduleEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            ScheduleEntry scheduleEntry = null;
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("JsonAssociationV1"))
            {
                scheduleEntry = context.deserialize(jsonObject.getAsJsonObject("JsonAssociationV1"), ScheduleAssociation.class);
            }
            else if (jsonObject.has("TiplocV1"))
            {
                scheduleEntry = context.deserialize(jsonObject.getAsJsonObject("TiplocV1"), Tiploc.class);
            }
            else if (jsonObject.has("JsonScheduleV1"))
            {
                JsonObject jsonScheduleObject = jsonObject.getAsJsonObject("JsonScheduleV1");
                scheduleEntry = context.deserialize(jsonScheduleObject, Schedule.class);
                ScheduleId id = context.deserialize(jsonScheduleObject, ScheduleId.class);
                ((Schedule)scheduleEntry).setId(id);
            }

            return scheduleEntry;
        }
    }

}
