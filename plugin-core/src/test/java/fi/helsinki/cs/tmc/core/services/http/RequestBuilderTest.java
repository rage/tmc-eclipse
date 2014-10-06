package fi.helsinki.cs.tmc.core.services.http;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestBuilderTest {

    private static final String URL = "url";
    
    private RequestBuilder builder;
    private RequestExecutor executor;
    private RequestExecutorFactory factory;

    @Before
    public void setUp() throws FailedHttpResponseException, IOException, InterruptedException {

        factory = mock(RequestExecutorFactory.class);
        builder = new RequestBuilder(factory);
        builder.setCredentials("username", "password");
        executor = mock(RequestExecutor.class);

        final BufferedHttpEntity e = mock(BufferedHttpEntity.class);
        when(executor.execute()).thenReturn(e);
        when(factory.createExecutor(anyString(), any(UsernamePasswordCredentials.class))).thenReturn(executor);
        when(factory.createExecutor(any(HttpPost.class), any(UsernamePasswordCredentials.class))).thenReturn(executor);

    }

    @Test
    public void getForBinaryCreatesExecutorWithCorrectUrlAndCredentials() throws Exception {

        builder.getForBinary(URL);
        verifyUrlAndCredentials(URL);
    }

    @Test
    public void getForTextCreatesExecutorWithCorrectUrlAndCredentials() throws Exception {

        builder.getForText(URL);
        verifyUrlAndCredentials(URL);
    }

    @Test
    public void postForBinaryCreatesExecutorWithCorrectRequestAndCredentials() throws Exception {

        builder.postForBinary(URL, new HashMap<String, String>());
        verifyRequestAndCredentials(URL);
    }

    @Test
    public void postForTextCreatesExecutorWithCorrectRequestAndCredentials() throws Exception {

        builder.postForText(URL, new HashMap<String, String>());
        verifyRequestAndCredentials(URL);
    }

    @Test
    public void rawPostForTextCreatesExecutorWithCorrectRequestAndCredentials() throws Exception {

        builder.rawPostForText(URL, new byte[0]);
        verifyRequestAndCredentials(URL);
    }

    @Test
    public void rawPostForTextWithExtraHeadersCreatesExecutorWithCorrectRequestAndCredentials() throws Exception {

        builder.rawPostForText(URL, new byte[0], new HashMap<String, String>());
        verifyRequestAndCredentials(URL);
    }

    @Test
    public void uploadFileForTextCreatesExecutorWithCorrectRequestAndCredentials() throws Exception {

        builder.uploadFileForTextDownload(URL, new HashMap<String, String>(), "", new byte[0]);
        verifyRequestAndCredentials(URL);
    }

    @Test
    public void makePostRequestHttpPostEntityHasNoContentIfParamsAreEmpty() throws Exception {

        when(factory.createExecutor(any(HttpPost.class), any(UsernamePasswordCredentials.class))).thenAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {

                final HttpPost post = (HttpPost) invocation.getArguments()[0];

                assertEquals(0, post.getEntity().getContent().available());
                return executor;
            }
        });
        builder.postForText(URL, new HashMap<String, String>());
    }

    @Test
    public void makePostRequestHttpPostHasEntitySetIfParamsAreNotEmpty() throws Exception {

        final Map<String, String> params = new HashMap<String, String>();
        params.put("key", "value");

        when(factory.createExecutor(any(HttpPost.class), any(UsernamePasswordCredentials.class))).thenAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {

                final HttpPost post = (HttpPost) invocation.getArguments()[0];

                assertFalse(post.getEntity().getContent().available() == 0);
                return executor;
            }
        });

        builder.postForText(URL, params);
    }

    @Test
    public void makeRawPostRequestHttpHeaderHasNoContentIfParamsAreEmpty() throws Exception {

        when(factory.createExecutor(any(HttpPost.class), any(UsernamePasswordCredentials.class))).thenAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {

                final HttpPost post = (HttpPost) invocation.getArguments()[0];

                assertEquals(0, post.getAllHeaders().length);
                return executor;
            }
        });
        builder.rawPostForText(URL, new byte[0]);
    }

    @Test
    public void makeRawPostRequestHttpPostHasHeaderSetIfParamsAreNotEmpty() throws Exception {

        final Map<String, String> params = new HashMap<String, String>();
        params.put("key", "value");

        when(factory.createExecutor(any(HttpPost.class), any(UsernamePasswordCredentials.class))).thenAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {

                final HttpPost post = (HttpPost) invocation.getArguments()[0];

                assertEquals(1, post.getAllHeaders().length);
                assertEquals("key", post.getAllHeaders()[0].getName());
                assertEquals("value", post.getAllHeaders()[0].getValue());

                return executor;
            }
        });

        builder.rawPostForText(URL, new byte[0], params);
    }

    private void verifyUrlAndCredentials(final String url) throws NoSuchFieldException, SecurityException, IllegalArgumentException,
    IllegalAccessException {

        final Field field = RequestBuilder.class.getDeclaredField("credentials");
        field.setAccessible(true);

        verify(factory, times(1)).createExecutor(url, (UsernamePasswordCredentials) field.get(builder));
    }

    private void verifyRequestAndCredentials(final String url) throws NoSuchFieldException, SecurityException, IllegalArgumentException,
    IllegalAccessException {

        final Field field = RequestBuilder.class.getDeclaredField("credentials");
        field.setAccessible(true);

        verify(factory, times(1)).createExecutor(any(HttpPost.class), eq((UsernamePasswordCredentials) field.get(builder)));
    }
}
