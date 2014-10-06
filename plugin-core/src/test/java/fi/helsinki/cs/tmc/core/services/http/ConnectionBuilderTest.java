package fi.helsinki.cs.tmc.core.services.http;

import fi.helsinki.cs.tmc.core.services.Settings;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConnectionBuilderTest {

    private ConnectionBuilder cb;

    @Before
    public void setUp() {

        cb = new ConnectionBuilder(Settings.getDefaultSettings());
    }

    @Test
    public void appendedUrlContainsApiVersion() {

        final String url = cb.getUrl("test");
        assertTrue(url.contains("api_version=" + ConnectionBuilder.API_VERSION));
    }

    @Test
    public void appendedUrlContainsClient() {

        final String url = cb.getUrl("test");
        assertTrue(url.contains("client=eclipse_plugin"));
    }

    @Test
    public void appendedUrlContainsClientVersion() {

        final String url = cb.getUrl("test");
        assertTrue(url.contains("client_version=0.0.1"));
    }

    @Test
    public void appendedUrlContainsCorrectRoot() {

        final String url = cb.getUrl("test");
        assertTrue(url.contains(Settings.getDefaultSettings().getServerBaseUrl() + "/test"));
    }
}
