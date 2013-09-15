package com.thegridman.rail.schedules.enums;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * STP (short-term planning) schedule indicator
 * C - STP cancellation of permanent schedule
 * N - New STP schedule (not an overlay)
 * O - STP overlay of permanent schedule
 * P - Permanent
 *
 * @author Jonathan Knight
 */
public enum STPIndicator
{
    C("Cancellation"),
    N("New"),
    O("Overlay"),
    P("Permanent");

    private String description;

    private STPIndicator(String description)
    {
        this.description = description;
    }

    @Override
    public String toString()
    {
        return description;
    }

    public static STPIndicator fromDescription(String description)
    {
        for (STPIndicator indicator : STPIndicator.values())
        {
            if (indicator.description.equals(description))
            {
                return indicator;
            }
        }

        throw new IllegalArgumentException("No STPIndicator for description " + description);
    }

    public static class JsonAdapter implements JsonDeserializer<STPIndicator>
    {
        public STPIndicator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws
        JsonParseException
        {
            String value = jsonElement.getAsString();
            return value != null ? STPIndicator.valueOf(value) : null;
        }
    }
}
