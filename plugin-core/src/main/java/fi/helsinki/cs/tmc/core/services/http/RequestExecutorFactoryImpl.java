package fi.helsinki.cs.tmc.core.services.http;

import fi.helsinki.cs.tmc.core.services.Settings;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;

/**
 * Implementation of the RequestExecutorFactory interface. Produces actual
 * RequestExecutor-objects
 *
 */
public class RequestExecutorFactoryImpl implements RequestExecutorFactory {

    private final Settings settings;

    public RequestExecutorFactoryImpl(final Settings settings) {

        this.settings = settings;
    }

    @Override
    public RequestExecutor createExecutor(final String url, final UsernamePasswordCredentials credentials) {

        return new RequestExecutor(url, new HttpClientFactoryImpl(), settings).setCredentials(credentials);
    }

    @Override
    public RequestExecutor createExecutor(final HttpPost request, final UsernamePasswordCredentials credentials) {

        return new RequestExecutor(request, new HttpClientFactoryImpl(), settings).setCredentials(credentials);
    }

}
