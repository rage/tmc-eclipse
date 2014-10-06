package fi.helsinki.cs.tmc.core.spyware.async;

import com.google.common.collect.Iterables;

import fi.helsinki.cs.tmc.core.spyware.services.EventStore;
import fi.helsinki.cs.tmc.core.spyware.services.LoggableEvent;

import java.io.IOException;
import java.util.ArrayDeque;

import org.junit.Before;
import org.junit.Test;
import org.mockito.exceptions.base.MockitoException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class SavingTaskTest {

    private SavingTask task;
    private ArrayDeque<LoggableEvent> sendQueue;
    private EventStore eventStore;

    @Before
    public void setUp() throws IOException {

        eventStore = mock(EventStore.class);

        sendQueue = new ArrayDeque<LoggableEvent>();
        sendQueue.add(new LoggableEvent("courseA", "ExerciseA", "eventTypeA", new byte[10]));
        sendQueue.add(new LoggableEvent("courseB", "ExerciseB", "eventTypeB", new byte[11]));
        sendQueue.add(new LoggableEvent("courseC", "ExerciseC", "eventTypeC", new byte[12]));

        task = new SavingTask(sendQueue, eventStore);

        doThrow(new MockitoException("Success")).when(eventStore).save(Iterables.toArray(sendQueue, LoggableEvent.class));

    }

    @Test(expected = MockitoException.class)
    public void runTest() throws IOException {

        task.run();
    }

}
