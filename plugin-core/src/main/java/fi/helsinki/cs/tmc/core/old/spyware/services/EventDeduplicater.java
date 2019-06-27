package fi.helsinki.cs.tmc.core.old.old.spyware.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fi.helsinki.cs.tmc.core.old.old.spyware.utility.ExceptionUtils;

/**
 * Forwards events to another receiver but discards consecutive events with the
 * same key and data. Only applied to certain event sources.
 */
public class EventDeduplicater implements EventReceiver {

    private final EventReceiver nextReceiver;

    private final Map<String, byte[]> lastHashByKey = new HashMap<String, byte[]>();

    public EventDeduplicater(final EventReceiver nextReceiver) {

        this.nextReceiver = nextReceiver;
    }

    @Override
    public synchronized void receiveEvent(final LoggableEvent event) {

        final byte[] prevHash = lastHashByKey.get(event.getKey());
        final byte[] newHash = hash(event.getData());
        final boolean changed = prevHash == null || !Arrays.equals(prevHash, newHash);
        if (changed) {
            nextReceiver.receiveEvent(event);
            lastHashByKey.put(event.getKey(), newHash);
        }
    }

    private byte[] hash(final byte[] data) {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (final NoSuchAlgorithmException ex) {
            throw ExceptionUtils.toRuntimeException(ex);
        }
        return md.digest(data);
    }

    @Override
    public void close() {

    }

}
