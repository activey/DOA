package pl.doa.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.doa.IDOA;
import pl.doa.entity.IEntity;
import pl.doa.entity.IEntityEvaluator;
import pl.doa.entity.ITransactionCallback;
import pl.doa.entity.event.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author activey
 * @date: 06.10.13 19:02
 */
public class EventsConsumer implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(EventsConsumer.class);

    private final IDOA doa;
    private final String transactionId;

    public EventsConsumer(IDOA doa, String transactionId) {
        this.doa = doa;
        this.transactionId = transactionId;
    }

    public EventsConsumer(IDOA doa) {
        this.doa = doa;
        this.transactionId = null;
    }

    private List<IEntityEvent> getEvents() {
        List<IEntityEvent> events = new ArrayList<IEntityEvent>();
        IEntityEvaluator evaluator = new IEntityEvaluator() {

            @Override
            public boolean isReturnableEntity(IEntity currentEntity) {
                if (transactionId == null) {
                    return true;
                }
                if (!(currentEntity instanceof IEntityEvent)) {
                    return false;
                }
                IEntityEvent event = (IEntityEvent) currentEntity;
                String eventTxId = (String) event
                        .getAttribute(IEntityEventDescription.EVENT_TX_ID);
                if (eventTxId == null) {
                    return false;
                }
                return transactionId.equals(eventTxId);
            }
        };
        for (IEntity event : doa.lookupEntitiesByLocation(IDOA.EVENTS_CONTAINER,
                evaluator)) {
            events.add((IEntityEvent) event);
        }
        return events;
    }

    @Override
    public void run() {
        for (final IEntityEvent event : getEvents()) {
            IEntity eventEntity = event.getSourceEntity();

            // wyciaganie listy sluchaczy dla konkretnego zdarzenia
            final List<IEntityEventListener> listeners = eventEntity
                    .getEventListeners(event);
            if (listeners == null || listeners.size() == 0) {
                log.debug(MessageFormat.format(
                        "There are no event listeners for: [{0}][{1}][{2}]",
                        eventEntity.getClass().getName(), eventEntity.getId() + "",
                        eventEntity.getLocation()));
                return;
            }
            log.debug(MessageFormat
                    .format("publishing entity event type: [{0}], for entity under location: [{1}]",
                            event.getClass().getName(),
                            event.getSourceEntity().getLocation()));
            log.debug("Event listeners count: " + listeners.size());
            for (IEntity doaEntity : listeners) {
                final IEntityEventListener listener = (IEntityEventListener) doaEntity;
                IEntityEventReceiver receiver = listener.getEventReceiver();
                try {
                    receiver.handleEvent(event);
                    // TODO usuwanie sluchacza

                    EntityEventType eventType = listener.getEventType();
                    if (eventType.isRemoveAfterProcessing()) {
                        if (log.isTraceEnabled()) {
                            log.debug("Removing event listener ...");
                        }
                        doa.doInTransaction(new ITransactionCallback<Object>() {

                            @Override
                            public Object performOperation()
                                    throws Exception {
                                listener.remove();
                                return null;
                            }
                        });

                    }
                    //
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            event.getSourceEntity().getDoa()
                    .doInTransaction(new ITransactionCallback() {

                        @Override
                        public Object performOperation() throws Exception {
                            log.debug("Removing published event: "
                                    + event.getEventType());
                            try {
                                return event.remove();
                            } catch (Exception ex) {
                                log.error(ex.getMessage());
                                return null;
                            }
                        }
                    });
        }
    }
}
