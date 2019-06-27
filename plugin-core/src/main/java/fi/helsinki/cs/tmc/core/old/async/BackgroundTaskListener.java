package fi.helsinki.cs.tmc.core.old.async;

/**
 * An interface that all background task listeners implement.
 */
public interface BackgroundTaskListener {

    /**
     * Called when background task is being started.
     */
    void onBegin();

    /**
     * Called when background task returns RETURN_SUCCESS.
     */
    void onSuccess();

    /**
     * Called when background task returns RETURN_FAILURE.
     */
    void onFailure();

    /**
     * Called when the background task returns RETURN_INTERRUPTED.
     */
    void onInterruption();

}
