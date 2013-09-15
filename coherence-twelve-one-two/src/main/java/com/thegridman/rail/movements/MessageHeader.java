package com.thegridman.rail.movements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

import java.lang.reflect.Type;

/**
 * @author Jonathan Knight
 */
@Portable
public class MessageHeader implements Comparable<MessageHeader>
{
    public static final int POF_MSG_TYPE = 0;

    @PortableProperty(value = POF_MSG_TYPE)
    private TrainMovementMessageType msgType;

    @PortableProperty(value = 1)
    private String sourceDevId;

    @PortableProperty(value = 2)
    private String userId;

    @PortableProperty(value = 3)
    private String originalDataSource;

    @PortableProperty(value = 4)
    private long msgQueueTimestamp;

    @PortableProperty(value = 5)
    private String sourceSystemId;

    public MessageHeader()
    {
    }

    public TrainMovementMessageType getMsgType()
    {
        return msgType;
    }

    public void setMsgType(TrainMovementMessageType msgType)
    {
        this.msgType = msgType;
    }

    public String getSourceDevId()
    {
        return sourceDevId;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getOriginalDataSource()
    {
        return originalDataSource;
    }

    public long getMsgQueueTimestamp()
    {
        return msgQueueTimestamp;
    }

    public String getSourceSystemId()
    {
        return sourceSystemId;
    }

    @Override
    public String toString()
    {
        return "MessageHeader{" +
               "msgType='" + msgType + '\'' +
               ", sourceDevId='" + sourceDevId + '\'' +
               ", userId='" + userId + '\'' +
               ", originalDataSource='" + originalDataSource + '\'' +
               ", msgQueueTimestamp=" + msgQueueTimestamp +
               ", sourceSystemId='" + sourceSystemId + '\'' +
               '}';
    }

    @Override
    public int compareTo(MessageHeader other)
    {
        if (other == null)
        {
            return 1;
        }

        long diff = this.msgQueueTimestamp - other.msgQueueTimestamp;
        if (diff < 0)
        {
            return -1;
        }

        if (diff > 0)
        {
            return 1;
        }

        return 0;
    }

    public static class JsonAdapter implements JsonDeserializer<MessageHeader>
    {
        public MessageHeader deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            MessageHeader header = new MessageHeader();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            int msgType = jsonObject.get("msg_type").getAsInt();
            header.msgType = TrainMovementMessageType.values()[msgType];
            header.sourceDevId = jsonObject.get("source_dev_id").getAsString();
            header.userId = jsonObject.get("user_id").getAsString();
            header.originalDataSource = jsonObject.get("original_data_source").getAsString();
            header.msgQueueTimestamp = jsonObject.get("msg_queue_timestamp").getAsLong();
            header.sourceSystemId = jsonObject.get("source_system_id").getAsString();
            return header;
        }
    }
}
