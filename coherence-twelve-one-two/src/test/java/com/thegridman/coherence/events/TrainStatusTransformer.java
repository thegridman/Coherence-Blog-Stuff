package com.thegridman.coherence.events;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.net.BackingMapContext;
import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.MapEvent;
import com.tangosol.util.MapEventTransformer;
import com.tangosol.util.ObservableMap;
import com.thegridman.rail.RailConstants;
import com.thegridman.rail.movements.TrainMovementMessage;
import com.thegridman.rail.movements.TrainMovementMessageId;
import com.thegridman.rail.movements.TrainMovementMovementMessage;
import com.thegridman.rail.movements.TrainStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jonathan Knight
 */
@Portable
public class TrainStatusTransformer implements MapEventTransformer
{
    @Override
    public MapEvent transform(MapEvent mapEvent)
    {
        BinaryEntry newEntry = (BinaryEntry) mapEvent.getNewEntry();
        if (newEntry == null)
        {
            return null;
        }

        TrainStatus trainStatus = (TrainStatus) newEntry.getValue();
        TrainMovementMessageId lastMessageId = trainStatus.getLastMessageId();

        BackingMapManagerContext context = newEntry.getContext();
        Binary binaryLastMessageId = (Binary) context.getKeyToInternalConverter().convert(lastMessageId);
        BackingMapContext movementsContext = context.getBackingMapContext(RailConstants.CACHENAME_TRAIN_MOVEMENTS);
        ObservableMap movementsBackingMap = movementsContext.getBackingMap();
        Binary binaryMovement = (Binary) movementsBackingMap.get(binaryLastMessageId);
        TrainMovementMessage message = (TrainMovementMessage) context.getValueFromInternalConverter().convert(binaryMovement);

        if (!(message instanceof TrainMovementMovementMessage))
        {
            return null;
        }

        TrainMovementMovementMessage movementMessage = (TrainMovementMovementMessage) message;

        Map<String,Object> data = new HashMap<>();
        data.put("Status", trainStatus.getStatus());
        data.put("Actual", movementMessage.getActualTimestamp());
        data.put("Planned", movementMessage.getPlannedTimestamp());
        data.put("Stanox", movementMessage.getLocStanox());

        return new MapEvent(mapEvent.getMap(), mapEvent.getId(), mapEvent.getKey(), null, data);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj != null && obj.getClass().equals(TrainStatusTransformer.class);
    }

    @Override
    public int hashCode()
    {
        return TrainStatusTransformer.class.hashCode();
    }
}
