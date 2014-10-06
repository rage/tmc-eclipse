package fi.helsinki.cs.tmc.core.ui;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class UserVisibleExceptionTest {

    @Test
    public void messageIsSet() {

        assertEquals("msg", new UserVisibleException("msg").getMessage());
    }

    @Test
    public void messageAndCauseAreSet() {

        final Throwable t = mock(Throwable.class);
        final UserVisibleException e = new UserVisibleException("msg", t);
        assertEquals("msg", e.getMessage());
        assertEquals(t, e.getCause());
    }

}
