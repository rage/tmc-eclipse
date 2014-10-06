package fi.helsinki.cs.tmc.core.services.http;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class HttpClientFactoryImplTest {

    private HttpClientFactory factory;

    @Before
    public void setUp() {

        factory = new HttpClientFactoryImpl();
    }

    @Test
    public void factoryReturnsObject() {

        assertNotNull(factory.makeHttpClient());
    }
}
