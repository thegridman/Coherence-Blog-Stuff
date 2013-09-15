package com.thegridman.rail.movements;

import com.tangosol.config.annotation.Injectable;
import com.tangosol.io.pof.reflect.SimplePofPath;
import com.tangosol.net.BackingMapContext;
import com.tangosol.net.BackingMapManagerContext;
import com.tangosol.net.events.EventDispatcher;
import com.tangosol.net.events.EventDispatcherAwareInterceptor;
import com.tangosol.net.events.EventInterceptor;
import com.tangosol.net.events.annotation.Interceptor;
import com.tangosol.net.events.partition.cache.EntryEvent;
import com.tangosol.util.Base;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.InvocableMap;
import com.tangosol.util.extractor.PofExtractor;
import com.thegridman.rail.RailConstants;

/**
 * @author Jonathan Knight
 */
@Interceptor(identifier = "train-status-updater", entryEvents = {EntryEvent.Type.INSERTING, EntryEvent.Type.UPDATING})
public class TrainStatusUpdateInterceptor extends Base implements EventInterceptor<EntryEvent>, EventDispatcherAwareInterceptor<EntryEvent>
{
    private final PofExtractor msgTypeExtractor;
    private BackingMapManagerContext context;

    public TrainStatusUpdateInterceptor()
    {
        this.msgTypeExtractor =
                new PofExtractor(TrainMovementMessageType.class,
                                 new SimplePofPath(new int[]{
                                         TrainMovementMessage.POF_HEADER,
                                         MessageHeader.POF_MSG_TYPE
                                 }));
    }

    @Override
    public void introduceEventDispatcher(String id, EventDispatcher eventDispatcher)
    {
        eventDispatcher.addEventInterceptor(this);
    }

    @Injectable
    public void setBackingMapContext(BackingMapManagerContext context)
    {
        this.context = context;
    }

    @Override
    public void onEvent(EntryEvent entryEvent)
    {
        for(BinaryEntry entry : entryEvent.getEntrySet())
        {
            processMessage(entry);
        }
    }

    private void processMessage(BinaryEntry entry)
    {
        TrainMovementMessageId messageId = (TrainMovementMessageId) entry.getKey();

        BackingMapManagerContext context = entry.getContext();
        BackingMapContext statusBackingMapContext = context.getBackingMapContext(RailConstants.CACHENAME_TRAIN_STATUS);

        TrainStatus.Id statusId = new TrainStatus.Id(messageId.getTrainId(), messageId.getScheduleId());
        Binary binaryStatusId = (Binary) context.getKeyToInternalConverter().convert(statusId);
        InvocableMap.Entry statusEntry = statusBackingMapContext.getBackingMapEntry(binaryStatusId);
        TrainStatus trainStatus;
        if (statusEntry.isPresent())
        {
            trainStatus = (TrainStatus) statusEntry.getValue();
        }
        else
        {
            trainStatus = new TrainStatus(statusId);
        }

        setStatus(trainStatus, entry);

        trainStatus.setLastMessageId(messageId);
        TrainMovementMessage message = (TrainMovementMessage) entry.getValue();
        trainStatus.setLastMessageTime(message.getHeader().getMsgQueueTimestamp());
        statusEntry.setValue(trainStatus);
    }

    private void setStatus(TrainStatus trainStatus, BinaryEntry entry)
    {
        TrainMovementMessageType type = (TrainMovementMessageType) entry.extract(msgTypeExtractor);
        switch (type)
        {
            case Activation:
                trainStatus.setStatus(TrainStatus.Status.Activated);
                break;
            case Cancellation:
                trainStatus.setStatus(TrainStatus.Status.Cancelled);
                break;
            case Reinstatement:
                trainStatus.setStatus(TrainStatus.Status.Cancelled);
                break;
            case Movement:
                TrainMovementMovementMessage movement = (TrainMovementMovementMessage) entry.getValue();
                long timeDiff = movement.getActualTimestamp() - movement.getPlannedTimestamp();
                trainStatus.setTimeDifference(timeDiff);
                if (timeDiff < -30000)
                {
                    trainStatus.setStatus(TrainStatus.Status.Early);
                }
                else if (timeDiff > 30000)
                {
                    trainStatus.setStatus(TrainStatus.Status.Delayed);
                }
                else
                {
                    trainStatus.setStatus(TrainStatus.Status.OnTime);
                }
                break;
            case UnidentifiedTrain:
                break;
            case ChangeOfOrigin:
                break;
            case ChangeOfIdentity:
                break;
            case ChangeOfLocation:
                trainStatus.setStatus(TrainStatus.Status.OnTime);
                break;
        }
    }
}
