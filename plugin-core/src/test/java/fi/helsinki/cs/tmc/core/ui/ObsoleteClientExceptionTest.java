package fi.helsinki.cs.tmc.core.ui;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ObsoleteClientExceptionTest {

    @Test
    public void containsCorrectString() {

        assertEquals("Please update the TMC plugin.\nUse Help -> Check for Updates.", new ObsoleteClientException().getMessage());
    }

}
