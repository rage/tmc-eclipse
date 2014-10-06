package fi.helsinki.cs.tmc.core.spyware.services;

import fi.helsinki.cs.tmc.core.async.tasks.SingletonTask;
import fi.helsinki.cs.tmc.core.services.Settings;
import fi.helsinki.cs.tmc.core.spyware.async.SavingTask;
import fi.helsinki.cs.tmc.core.spyware.async.SendingTask;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventSendBufferTest {

    private EventSendBuffer buffer;
    private EventStore store;
    private Settings settings;
    private ArrayDeque<LoggableEvent> sendQueue;
    private AtomicInteger eventsToRemoveAfterSend;

    private SendingTask sendingTask;
    private SavingTask savingTask;

    @Before
    public void setUp() throws Exception {

        settings = mock(Settings.class);
        when(settings.isSpywareEnabled()).thenReturn(true);
        initializeEventStore();
        initializeSendQueue();
        eventsToRemoveAfterSend = new AtomicInteger();

        sendingTask = mock(SendingTask.class);
        savingTask = mock(SavingTask.class);

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        buffer = new EventSendBuffer(store, settings, sendQueue, new SingletonTask(sendingTask, scheduler), new SingletonTask(savingTask, scheduler),
                eventsToRemoveAfterSend);

    }

    @Test
    public void doesNotReceiveEventIfSpywareIsDisabled() {

        when(settings.isSpywareEnabled()).thenReturn(false);

        buffer.receiveEvent(new LoggableEvent("a", "b", "c", new byte[1], "d"));

        assertEquals(6, sendQueue.size());
    }

    @Test
    public void receiveEventTest() {

        buffer.setSendingInterval(1);
        buffer.receiveEvent(new LoggableEvent("a", "b", "c", new byte[1], "d"));

        try {
            Thread.sleep(50);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(7, sendQueue.size());
        verify(sendingTask, atLeastOnce()).run();
    }

    @Test
    public void closeTest() {

        buffer.setSavingInterval(1);
        buffer.close();

        verify(savingTask, times(1)).run();
    }

    private void initializeEventStore() throws IOException {

        store = mock(EventStore.class);
        final LoggableEvent[] arr = new LoggableEvent[3];
        arr[0] = new LoggableEvent("course1", "exercise1", "eventType1", new byte[1], "metadata1");
        arr[1] = new LoggableEvent("course2", "exercise2", "eventType2", new byte[2], "metadata2");
        arr[2] = new LoggableEvent("course3", "exercise3", "eventType3", new byte[3], "metadata3");

        when(store.load()).thenReturn(arr);
    }

    private void initializeSendQueue() {

        sendQueue = new ArrayDeque<LoggableEvent>();
        sendQueue.add(new LoggableEvent("course1", "exercise1", "eventType1", new byte[1], "metadata1"));
        sendQueue.add(new LoggableEvent("course2", "exercise2", "eventType2", new byte[2], "metadata2"));
        sendQueue.add(new LoggableEvent("course3", "exercise3", "eventType3", new byte[3], "metadata3"));
    }
}
