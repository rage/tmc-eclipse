package fi.helsinki.cs.tmc.core.old.old.spyware.services;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.helsinki.cs.tmc.core.old.old.async.tasks.SingletonTask;
import fi.helsinki.cs.tmc.core.old.old.services.Settings;
import fi.helsinki.cs.tmc.core.old.old.spyware.utility.Cooldown;

/**
 * Buffers {@link LoggableEvent}s and sends them to the server and/or syncs them
 * to the disk periodically.
 */
public class EventSendBuffer implements EventReceiver {

    public static final long DEFAULT_SEND_INTERVAL = 3 * 60 * 1000;
    public static final long DEFAULT_SAVE_INTERVAL = 1 * 60 * 1000;
    public static final int DEFAULT_MAX_EVENTS = 64 * 1024;
    public static final int DEFAULT_AUTOSEND_THREHSOLD = DEFAULT_MAX_EVENTS / 2;
    public static final int DEFAULT_AUTOSEND_COOLDOWN = 30 * 1000;

    private static final Logger LOG = Logger.getLogger(EventSendBuffer.class.getName());

    private final SingletonTask savingTask;
    private final SingletonTask sendingTask;

    private final EventStore eventStore;
    private final Settings settings;

    // The following variables must only be accessed with a lock on sendQueue.
    private final ArrayDeque<LoggableEvent> sendQueue;
    private final int maxEvents = DEFAULT_MAX_EVENTS;
    private final int autosendThreshold = DEFAULT_AUTOSEND_THREHSOLD;
    private final Cooldown autosendCooldown;
    private final AtomicInteger eventsToRemoveAfterSend;

    public EventSendBuffer(final EventStore store, final Settings settings, final ArrayDeque<LoggableEvent> sendQueue,
            final SingletonTask sendingTask, final SingletonTask savingTask, final AtomicInteger eventsToRemoveAfterSend) {

        eventStore = store;
        this.settings = settings;
        this.sendQueue = sendQueue;
        this.eventsToRemoveAfterSend = eventsToRemoveAfterSend;

        autosendCooldown = new Cooldown(DEFAULT_AUTOSEND_COOLDOWN);

        this.sendingTask = sendingTask;
        this.savingTask = savingTask;

        try {
            List<LoggableEvent> initialEvents = Arrays.asList(eventStore.load());
            initialEvents = initialEvents.subList(0, Math.min(maxEvents, initialEvents.size()));
            this.sendQueue.addAll(initialEvents);
        } catch (final IOException ex) {
            LOG.log(Level.WARNING, "Failed to read events from event store", ex);
        } catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Failed to read events from event store", ex);
        }

        this.sendingTask.setInterval(DEFAULT_SEND_INTERVAL);
        this.savingTask.setInterval(DEFAULT_SAVE_INTERVAL);
    }

    public void setSendingInterval(final long interval) {

        sendingTask.setInterval(interval);
    }

    public void setSavingInterval(final long interval) {

        savingTask.setInterval(interval);
    }

    private void sendNow() {

        sendingTask.start();
    }

    public void waitUntilCurrentSendingFinished(final long timeout) throws TimeoutException, InterruptedException {

        sendingTask.waitUntilFinished(timeout);
    }

    @Override
    public void receiveEvent(final LoggableEvent event) {

        if (!settings.isSpywareEnabled()) {
            return;
        }

        synchronized (sendQueue) {
            if (sendQueue.size() >= maxEvents) {
                sendQueue.pop();
                eventsToRemoveAfterSend.decrementAndGet();
            }
            sendQueue.add(event);

            maybeAutosend();
        }
    }

    private void maybeAutosend() {

        if (sendQueue.size() >= autosendThreshold && autosendCooldown.isExpired()) {
            autosendCooldown.start();
            sendNow();
        }
    }

    /**
     * Stops sending any more events.
     *
     * Buffer manipulation methods may still be called.
     */
    @Override
    public void close() {

        final long delayPerWait = 2000;

        try {
            sendingTask.unsetInterval();
            savingTask.unsetInterval();

            savingTask.waitUntilFinished(delayPerWait);
            savingTask.start();
            savingTask.waitUntilFinished(delayPerWait);
            sendingTask.waitUntilFinished(delayPerWait);

        } catch (final TimeoutException ex) {
            LOG.log(Level.WARNING, "Time out when closing EventSendBuffer", ex);
        } catch (final InterruptedException ex) {
            LOG.log(Level.WARNING, "Closing EventSendBuffer interrupted", ex);
        }

    }

}
