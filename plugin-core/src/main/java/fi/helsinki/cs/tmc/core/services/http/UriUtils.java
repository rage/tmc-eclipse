package fi.helsinki.cs.tmc.core.services.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * Helper utility class for URI operations.
 *
 */
final class UriUtils {

    private UriUtils() {

    }

    /**
     * Returns the given string with query parameters.
     *
     * @param uri
     *            Original string
     * @param name
     *            name
     * @param value
     *            value
     * @return String with uri + name + value
     */
    public static String withQueryParam(final String uri, final String name, final String value) {

        return withQueryParam(URI.create(uri), name, value).toString();
    }

    /**
     * Returns the given string with query parameters.
     *
     * @param uri
     *            Original string
     * @param name
     *            name
     * @param value
     *            value
     * @return URI with uri + name + value
     */
    public static URI withQueryParam(final URI uri, final String name, final String value) {

        final List<NameValuePair> pairs = URLEncodedUtils.parse(uri, "UTF-8");
        final Iterator<NameValuePair> i = pairs.iterator();
        while (i.hasNext()) {
            if (i.next().getName().equals(name)) {
                i.remove();
                break;
            }
        }
        final List<NameValuePair> newPairs = new ArrayList<NameValuePair>(pairs.size() + 1);
        newPairs.addAll(pairs);

        newPairs.add(new BasicNameValuePair(name, value));
        final String newQuery = URLEncodedUtils.format(newPairs, "UTF-8");
        try {
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), newQuery, uri.getFragment());
        } catch (final URISyntaxException ex) {
            System.out.println("got URISyntaxEception " + ex.getMessage());
            throw new IllegalArgumentException(ex);
        }
    }
}
