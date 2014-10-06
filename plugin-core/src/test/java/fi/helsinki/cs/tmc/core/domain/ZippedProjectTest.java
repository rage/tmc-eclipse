package fi.helsinki.cs.tmc.core.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ZippedProjectTest {

    private ZippedProject zp;

    @Before
    public void setUp() {

        zp = new ZippedProject();
    }

    @Test
    public void canSetAndGetBytes() {

        final byte[] bytes = { 1, 0, 1 };
        zp.setBytes(bytes);
        assertEquals(bytes, zp.getBytes());
    }
}
