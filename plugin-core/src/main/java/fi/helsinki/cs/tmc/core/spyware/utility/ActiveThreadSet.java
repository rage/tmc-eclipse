package fi.helsinki.cs.tmc.core.spyware.utility;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A set of active or unstarted threads.
 *
 * <p>
 * Note that this is not a thread group. If a thread T1 in a thread set spawns
 * another thread T2 then T2 will <em>not</em> be in the thread set.
 */
public class ActiveThreadSet {

    private final List<Thread> threads;

    public ActiveThreadSet() {

        threads = new LinkedList<Thread>();
    }

    public void addThread(final Thread thread) {

        cleanUp();
        threads.add(thread);
    }

    /**
     * Waits for all threads to terminate.
     */
    public void joinAll() throws InterruptedException {

        while (!threads.isEmpty()) {
            final Thread thread = cleanUpToFirstUnterminated();
            if (thread != null) {
                thread.join();
            }
        }
    }

    private void cleanUp() {

        final Iterator<Thread> i = threads.iterator();
        while (i.hasNext()) {
            final Thread t = i.next();
            if (t.getState() == Thread.State.TERMINATED) {
                i.remove();
            }
        }
    }

    private Thread cleanUpToFirstUnterminated() {

        final Iterator<Thread> i = threads.iterator();
        while (i.hasNext()) {
            final Thread t = i.next();
            if (t.getState() == Thread.State.TERMINATED) {
                i.remove();
            } else {
                return t;
            }
        }
        return null;
    }

}
