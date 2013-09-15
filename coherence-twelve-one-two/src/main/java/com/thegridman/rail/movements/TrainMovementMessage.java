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
public abstract class TrainMovementMessage implements Comparable<TrainMovementMessage>
{
    public static final int POF_ID = 0;
    public static final int POF_HEADER = 1;

    @PortableProperty(value = POF_ID)
    private TrainMovementMessageId id;

    @PortableProperty(value = POF_HEADER)
    private MessageHeader header;

    /**
     * Operating company ID as per TOC Codes
     */
    @PortableProperty(value = 2)
    private String toc_id;

    /**
     * Train service code as per schedule
     */
    @PortableProperty(value = 3)
    private String train_service_code;

    /**
     * The TOPS train file address, if applicable
     */
    @PortableProperty(value = 4)
    private String train_file_address;

    private transient String jsonText;

    protected TrainMovementMessage(String train_id)
    {
        id = new TrainMovementMessageId(train_id);
        header = new MessageHeader();
    }

    public MessageHeader getHeader()
    {
        return header;
    }

    public TrainMovementMessageId getId()
    {
        return id;
    }

    public String getTocId()
    {
        return toc_id;
    }

    public String getTrainFileAddress()
    {
        return train_file_address;
    }

    public String getTrainServiceCode()
    {
        return train_service_code;
    }

    public String getJsonText()
    {
        return jsonText;
    }

    @Override
    public int compareTo(TrainMovementMessage other)
    {
        if (this.header == null && other.header == null)
        {
            return 0;
        }
        if (this.header != null && other.header == null)
        {
            return 1;
        }
        if (this.header == null)
        {
            return -1;
        }
        return this.header.compareTo(other.header);
    }

    public static class JsonAdapter implements JsonDeserializer<TrainMovementMessage>
    {
        public TrainMovementMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject headerJsonObject = jsonObject.get("header").getAsJsonObject();
            JsonObject bodyJsonObject = jsonObject.get("body").getAsJsonObject();

            try
            {
                MessageHeader header = context.deserialize(headerJsonObject, MessageHeader.class);

                TrainMovementMessage message;
                switch (header.getMsgType())
                {
                    case Activation:
                        message = context.deserialize(bodyJsonObject, TrainMovementActivationMessage.class);
                        break;
                    case Cancellation:
                        message = context.deserialize(bodyJsonObject, TrainMovementCancellationMessage.class);
                        break;
                    case Movement:
                        message = context.deserialize(bodyJsonObject, TrainMovementMovementMessage.class);
                        break;
                    case UnidentifiedTrain:
                        message = context.deserialize(bodyJsonObject, TrainMovementUnidentifiedTrainMessage.class);
                        break;
                    case Reinstatement:
                        message = context.deserialize(bodyJsonObject, TrainMovementReinstatementMessage.class);
                        break;
                    case ChangeOfOrigin:
                        message = context.deserialize(bodyJsonObject, TrainMovementChangeOfOriginMessage.class);
                        break;
                    case ChangeOfIdentity:
                        message = context.deserialize(bodyJsonObject, TrainMovementChangeOfIdentityMessage.class);
                        break;
                    case ChangeOfLocation:
                        message = context.deserialize(bodyJsonObject, TrainMovementChangeOfLocationMessage.class);
                        break;
                    default:
                        throw new JsonParseException("Unsupported message type: " + header.getMsgType());
                }

                JsonElement trainIdElement = bodyJsonObject.get("train_id");
                String trainId = trainIdElement.getAsString();
                message.id = new TrainMovementMessageId(trainId);
                message.header = header;
                message.jsonText = jsonElement.toString();

                return message;
            }
            catch (JsonParseException e)
            {
                System.out.println(bodyJsonObject);
                e.printStackTrace();
                return null;
            }
        }
    }
}
