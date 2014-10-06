package fi.helsinki.cs.tmc.core.spyware.utility;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * Stats padding is gooood.
 *
 */
public class CooldownTest {

    private static final int COOLDOWN = 1;
    
    private Cooldown cooldown;

    @Before
    public void setUp() {

        cooldown = new Cooldown(COOLDOWN);
    }

    @Test
    public void getDurationInMillisReturnsCorrectValue() {

        assertEquals(COOLDOWN, cooldown.getDurationMillis());
    }

    @Test
    public void setterSetsCorrectTime() {

        cooldown.setDurationMillis(COOLDOWN * 2);
        assertEquals(COOLDOWN * 2, cooldown.getDurationMillis());

    }

    @Test
    public void cooldownExpiresCorrectly() throws InterruptedException {

        cooldown.start();
        Thread.sleep(COOLDOWN * 2);
        assertTrue(cooldown.isExpired());
    }

    @Test
    public void longCooldownDoesNotExpireImmediately() throws InterruptedException {

        cooldown.setDurationMillis(2500000);
        cooldown.start();
        assertFalse(cooldown.isExpired());
    }
}
