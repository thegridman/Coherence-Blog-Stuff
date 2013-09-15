package com.thegridman.rail.schedules.enums;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * @author Jonathan Knight
 */
public enum TransactionType
{
    Create,
    Delete;

    public static class JsonAdapter implements JsonDeserializer<TransactionType>
    {
        public TransactionType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws
        JsonParseException
        {
            String value = jsonElement.getAsString();
            return value != null ? TransactionType.valueOf(value) : null;
        }
    }

}

