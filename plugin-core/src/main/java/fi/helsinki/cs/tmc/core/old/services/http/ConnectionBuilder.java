package fi.helsinki.cs.tmc.core.old.old.services.http;

import fi.helsinki.cs.tmc.core.old.old.services.Settings;

/**
 * Builder class for http connections.
 */
class ConnectionBuilder {

    public static final int API_VERSION = 7;

    private final Settings settings;

    public ConnectionBuilder(final Settings settings) {

        this.settings = settings;
    }

    /**
     * Gets URL to server with necessary APi call parameters.
     *
     * @param extension
     *            Extension to be added to the base URL
     * @return Complete URL
     */
    public String getUrl(final String extension) {

        return addApiCallQueryParameters(settings.getServerBaseUrl() + "/" + extension);
    }

    /**
     * Adds necessary URL parameters to request so that server accepts the
     * request.
     *
     * @param url
     *            URL in question
     * @return URL with parameters
     */
    public String addApiCallQueryParameters(final String url) {

        String newUrl = url;

        newUrl = UriUtils.withQueryParam(newUrl, "api_version", "" + API_VERSION);
        newUrl = UriUtils.withQueryParam(newUrl, "client", "eclipse_plugin");
        newUrl = UriUtils.withQueryParam(newUrl, "client_version", getClientVersion());

        return newUrl;
    }

    private static String getClientVersion() {

        return "0.0.1";
    }

    /**
     * Creates and returns RequestBuilder.
     *
     * @return RequestBuilder that will build any requests
     */
    public RequestBuilder createConnection() {

        return new RequestBuilder(new RequestExecutorFactoryImpl(settings)).setCredentials(settings.getUsername(), settings.getPassword());
    }
}
