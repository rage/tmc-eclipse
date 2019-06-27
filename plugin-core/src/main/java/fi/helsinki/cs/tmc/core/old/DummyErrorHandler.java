package fi.helsinki.cs.tmc.core.old;

/**
 * Dummy error handler that does nothing. Default for the Core until
 * IDE-specific implementation is provided.
 */
public class DummyErrorHandler implements TMCErrorHandler {

    @Override
    public void raise(final String message) {

    }

    @Override
    public void handleException(final Exception e) {

    }

    @Override
    public void handleManualException(final String errorMessage) {

    }

}
