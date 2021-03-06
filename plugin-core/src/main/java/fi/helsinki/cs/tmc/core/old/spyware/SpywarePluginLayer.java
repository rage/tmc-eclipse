package fi.helsinki.cs.tmc.core.old.spyware;

import java.io.Closeable;

import fi.helsinki.cs.tmc.core.old.spyware.services.DocumentChangeHandler;
import fi.helsinki.cs.tmc.core.old.spyware.services.EventReceiver;
import fi.helsinki.cs.tmc.core.old.spyware.services.SnapshotTaker;
import fi.helsinki.cs.tmc.core.old.spyware.utility.ActiveThreadSet;

public class SpywarePluginLayer implements Closeable {

    private final ActiveThreadSet activeThreads;
    private final EventReceiver receiver;
    private final SnapshotTaker taker;
    private final DocumentChangeHandler documentHandler;

    public SpywarePluginLayer(final ActiveThreadSet activeThreads, final EventReceiver receiver, final SnapshotTaker taker,
            final DocumentChangeHandler documentHandler) {

        this.activeThreads = activeThreads;
        this.receiver = receiver;
        this.taker = taker;
        this.documentHandler = documentHandler;
    }

    public void takeSnapshot(final SnapshotInfo info) {

        taker.execute(info);
    }

    public void documentChange(final DocumentInfo info) {

        documentHandler.handleEvent(info);
    }

    @Override
    public void close() {

        // TODO run in a separate thread
        try {
            activeThreads.joinAll();
        } catch (final InterruptedException e) {
            // do nothing
        }
    }

}
