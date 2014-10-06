package fi.helsinki.cs.tmc.core.spyware.async;

import com.google.common.collect.Iterables;

import fi.helsinki.cs.tmc.core.spyware.services.EventStore;
import fi.helsinki.cs.tmc.core.spyware.services.LoggableEvent;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SavingTask implements Runnable {

    private final ArrayDeque<LoggableEvent> sendQueue;
    private final EventStore eventStore;

    public SavingTask(final ArrayDeque<LoggableEvent> sendQueue, final EventStore eventStore) {

        this.sendQueue = sendQueue;
        this.eventStore = eventStore;
    }

    @Override
    public void run() {

        try {
            LoggableEvent[] eventsToSave;
            synchronized (sendQueue) {
                eventsToSave = Iterables.toArray(sendQueue, LoggableEvent.class);
            }
            eventStore.save(eventsToSave);
        } catch (final IOException ex) {
            Logger.getLogger(SavingTask.class.getName()).log(Level.WARNING, "Failed to save events", ex);
        }
    }
}
