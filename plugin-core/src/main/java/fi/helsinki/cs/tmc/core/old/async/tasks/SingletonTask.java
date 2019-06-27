package fi.helsinki.cs.tmc.core.old.old.async.tasks;

import com.google.common.util.concurrent.Futures;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SingletonTask {

    private final ScheduledExecutorService scheduler;
    private final Runnable runnable;

    private final Runnable autostartRunnable = new Runnable() {

        @Override
        public void run() {

            start();
        }
    };

    private Future<?> task;
    private ScheduledFuture<?> autostartTask;

    public SingletonTask(final Runnable runnable, final ScheduledExecutorService scheduler) {

        this.scheduler = scheduler;
        this.runnable = runnable;
        task = Futures.immediateFuture(null);
    }

    public synchronized void setInterval(final long delay) {

        unsetInterval();

        autostartTask = scheduler.scheduleWithFixedDelay(autostartRunnable, delay, delay, TimeUnit.MILLISECONDS);
    }

    public synchronized void unsetInterval() {

        if (autostartTask != null) {
            autostartTask.cancel(true);
            autostartTask = null;
        }
    }

    /**
     * Starts the task unless it's already running.
     */
    public synchronized void start() {

        if (task.isDone()) {
            task = scheduler.submit(runnable);
        }
    }

    /**
     * Waits for the task to finish if it is currently running.
     *
     * Note: this method does not indicate in any way whether the task succeeded
     * or failed.
     *
     * @param timeout
     *            Maximum time in milliseconds to wait before throwing a
     *            TimeoutException.
     */
    public synchronized void waitUntilFinished(final long timeout) throws TimeoutException, InterruptedException {

        try {
            task.get(timeout, TimeUnit.MILLISECONDS);
        } catch (final ExecutionException ex) {
            // Ignore
        }
    }

    public synchronized boolean isRunning() {

        return !task.isDone();
    }
}
