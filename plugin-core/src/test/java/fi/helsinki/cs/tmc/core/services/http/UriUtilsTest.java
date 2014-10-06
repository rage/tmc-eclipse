package fi.helsinki.cs.tmc.core.services.http;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UriUtilsTest {

    @Test
    public void testWithQueryParamString() {

        final String uri = UriUtils.withQueryParam("http://tmc.mooc.fi", "test", "val");

        assertEquals(uri.toString(), "http://tmc.mooc.fi?test=val");
    }

    @Test
    public void testWithQueryParamsURI() throws URISyntaxException {

        final URI uri = UriUtils.withQueryParam(new URI("http://tmc.mooc.fi"), "key", "value");

        assertEquals(uri.toString(), "http://tmc.mooc.fi?key=value");
    }
}
