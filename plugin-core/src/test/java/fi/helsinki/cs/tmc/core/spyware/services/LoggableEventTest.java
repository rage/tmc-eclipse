package fi.helsinki.cs.tmc.core.spyware.services;

import fi.helsinki.cs.tmc.core.domain.Exercise;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class LoggableEventTest {

    private LoggableEvent le1;
    private LoggableEvent le2;

    private final byte[] b = { 1, 1, 1 };

    @Before
    public void setUp() throws Exception {

        le1 = new LoggableEvent(Mockito.mock(Exercise.class), "a", b);
        le2 = new LoggableEvent(Mockito.mock(Exercise.class), "a", b, "b");
    }

    @Test
    public void testGetters() {

        assertEquals(le1.getData(), le2.getData());
        assertEquals(le2.getMetadata(), "b");

        // assertEquals(le2.getHappenedAt(), System.currentTimeMillis());

        // assertEquals(le2.toString(), "LoggableEvent{" + "courseName=" +
        // courseName + ", exerciseName=" + exerciseName + ", eventType=" +
        // eventType + ", happenedAt=" + happenedAt + ", systemNanotime=" +
        // systemNanotime + ", key=" + key + ", metadata=" + metadata +
        // ", data=" + new String(data) + "}");
    }
}
