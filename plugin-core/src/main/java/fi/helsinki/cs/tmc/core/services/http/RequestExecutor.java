package fi.helsinki.cs.tmc.core.services.http;

import fi.helsinki.cs.tmc.core.services.Settings;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Downloads a single file over HTTP into memory.
 *
 * If the response was not a successful one (status code 2xx) then a
 * {@link FailedHttpResponseException} with a preloaded buffered entity is
 * thrown.
 */
class RequestExecutor {

    private static final Logger LOG = Logger.getLogger(RequestExecutor.class.getName());

    private final HttpUriRequest request;
    private final HttpClientFactory factory;
    private final Settings settings;

    private UsernamePasswordCredentials credentials;

    /**
     * Constructor that will default to HTTP get request with the given URL.
     *
     * @param url
     *            URL where the post request will be sent
     * @param factory
     *            HTTP client factory that will construct the http client that
     *            will be used for network communication
     * @param settings
     *            Program settings. Class will update the login status of the
     *            user
     */
    /* package */RequestExecutor(final String url, final HttpClientFactory factory, final Settings settings) {

        this(new HttpGet(url), factory, settings);
    }

    /**
     * Constructor that accepts HTTP requests from user.
     *
     * @param request
     *            The request that will be sent to the server
     * @param factory
     *            HTTP client factory that will construct the http client that
     *            will be used for network communication
     * @param settings
     *            Program settings. Class will update the login status of the
     *            user
     */
    /* package */RequestExecutor(final HttpUriRequest request, final HttpClientFactory factory, final Settings settings) {

        this.request = request;
        this.factory = factory;
        this.settings = settings;
        if (request.getURI().getUserInfo() != null) {
            credentials = new UsernamePasswordCredentials(request.getURI().getUserInfo());
        }

    }

    /**
     * Sets the credentials for future use.
     *
     * @param username
     *            username
     * @param password
     *            passowrd
     * @return Returns itself to enable builder pattern
     */
    public RequestExecutor setCredentials(final String username, final String password) {

        return setCredentials(new UsernamePasswordCredentials(username, password));
    }

    /**
     * Sets the credentials for future use.
     *
     * @param credentials
     *            UsernamePasswordCredentials object containing the username and
     *            password
     * @return Returns itself to enable builder pattern
     */
    public RequestExecutor setCredentials(final UsernamePasswordCredentials credentials) {

        this.credentials = credentials;
        return this;
    }

    /**
     * Executes the HTTP request.
     *
     * @return BufferedHttpEntity containing the result of the request
     * @throws IOException
     *             Throws IOException if download fails, if server response
     *             contains no entity or if various java buffers fail internally
     * @throws InterruptedException
     *             Throws InterruptedException if request is aborted or if
     *             authentication fails
     * @throws FailedHttpResponseException
     *             Throws FailedHttpResponseException if status code is not 2xx
     */
    public BufferedHttpEntity execute() throws IOException, InterruptedException, FailedHttpResponseException {

        final CloseableHttpClient httpClient = factory.makeHttpClient();

        return executeRequest(httpClient);
    }

    private BufferedHttpEntity executeRequest(final HttpClient httpClient) throws IOException, InterruptedException, FailedHttpResponseException {

        HttpResponse response = null;

        try {
            if (credentials != null) {
                request.addHeader(new BasicScheme(Charset.forName("UTF-8")).authenticate(credentials, request, null));
            }

            response = httpClient.execute(request);

        } catch (final IOException ex) {
            if (request.isAborted()) {
                throw new InterruptedException();
            } else {
                throw new IOException("Download failed: " + ex.getMessage(), ex);
            }
        } catch (final AuthenticationException ex) {
            throw new InterruptedException();
        }

        return handleResponse(response);
    }

    private BufferedHttpEntity handleResponse(final HttpResponse response) throws IOException, FailedHttpResponseException {

        final int responseCode = response.getStatusLine().getStatusCode();
        settings.setLoggedIn(false);
        if (response.getEntity() == null) {
            throw new IOException("HTTP " + responseCode + " with no response");
        }

        final BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
        
        // Ensure it's loaded into memory
        EntityUtils.consume(entity); 
        
        if (success(responseCode)) {
            settings.setLoggedIn(true);
            return entity;
        } else {
            throw new FailedHttpResponseException(responseCode, entity);
        }
    }

    /**
     * Returns true for statuscodes in the 2xx (success) range. SC_OK = 200,
     * SC_MULTIPLE_CHOICES = 300.
     */
    private boolean success(final int responseCode) {

        return HttpStatus.SC_OK <= responseCode && responseCode < HttpStatus.SC_MULTIPLE_CHOICES;
    }

    /**
     * For testing.
     */
    @Override
    public String toString() {

        return LOG.getName() + "\n" + request.getMethod() + "\n" + request.getURI().toString() + "\n" + credentials.getUserName() + " " +
                credentials.getPassword();
    }
}
