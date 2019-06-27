package fi.helsinki.cs.tmc.core.old.old.spyware.async;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.helsinki.cs.tmc.core.old.old.async.tasks.SingletonTask;
import fi.helsinki.cs.tmc.core.old.old.domain.Course;
import fi.helsinki.cs.tmc.core.old.old.services.CourseDAO;
import fi.helsinki.cs.tmc.core.old.old.services.Settings;
import fi.helsinki.cs.tmc.core.old.old.services.http.ServerManager;
import fi.helsinki.cs.tmc.core.old.old.spyware.services.LoggableEvent;

public class SendingTask implements Runnable {

    private static final Logger LOG = Logger.getLogger(SendingTask.class.getName());
    private static final int MAX_EVENTS_PER_SEND = 500;

    private final ArrayDeque<LoggableEvent> sendQueue;
    private final ServerManager serverManager;
    private final CourseDAO courseDAO;
    private final Settings settings;
    private final Random random;
    private final AtomicInteger eventsToRemoveAfterSend;

    private final SingletonTask savingTask;

    public SendingTask(final ArrayDeque<LoggableEvent> sendQueue,
                       final ServerManager serverManager,
                       final CourseDAO courseDAO,
                       final Settings settings,
                       final SingletonTask savingTask,
                       final AtomicInteger eventsToRemoveAfterSend) {

        this.sendQueue = sendQueue;
        this.serverManager = serverManager;
        this.courseDAO = courseDAO;
        this.settings = settings;
        this.savingTask = savingTask;
        this.random = new Random();
        this.eventsToRemoveAfterSend = eventsToRemoveAfterSend;
    }

    @Override
    public void run() {

        if (!settings.isSpywareEnabled()) {
            return;
        }

        boolean shouldSendMore;

        do {
            final List<LoggableEvent> eventsToSend = copyEventsToSendFromQueue();
            if (eventsToSend.isEmpty()) {
                return;
            }

            synchronized (sendQueue) {
                shouldSendMore = sendQueue.size() > eventsToSend.size();
            }

            final String url = pickDestinationUrl();
            if (url == null) {
                return;
            }

            LOG.log(Level.INFO, "Sending {0} events to {1}", new Object[] { eventsToSend.size(), url });

            doSend(eventsToSend, url);
        } while (shouldSendMore);
    }

    private List<LoggableEvent> copyEventsToSendFromQueue() {

        synchronized (sendQueue) {
            final List<LoggableEvent> eventsToSend = new ArrayList<LoggableEvent>(sendQueue.size());

            final Iterator<LoggableEvent> i = sendQueue.iterator();
            while (i.hasNext() && eventsToSend.size() < MAX_EVENTS_PER_SEND) {
                eventsToSend.add(i.next());
            }

            eventsToRemoveAfterSend.set(eventsToSend.size());

            return eventsToSend;
        }
    }

    private String pickDestinationUrl() {

        final Course course = courseDAO.getCurrentCourse(settings);
        if (course == null) {
            LOG.log(Level.FINE, "Not sending events because no course selected");
            return null;
        }

        final List<String> urls = course.getSpywareUrls();
        if (urls == null || urls.isEmpty()) {
            LOG.log(Level.INFO, "Not sending events because no URL provided by server");
            return null;
        }

        final String url = urls.get(random.nextInt(urls.size()));

        return url;

        // url for localhost debugging, assuming spyware server
        // runs at port 3101
        // return "http://127.0.0.1:3101";
    }

    private void doSend(final List<LoggableEvent> eventsToSend, final String url) {

        try {
            serverManager.sendEventLogs(url, eventsToSend);
            LOG.log(Level.INFO, "Sent {0} events successfully to {1}", new Object[] { eventsToSend.size(), url });

        } catch (final Exception ex) {
            LOG.log(Level.INFO, "Failed to send {0} events to {1}: " + ex.getMessage(), new Object[] { eventsToSend.size(), url });
            return;
        }
        removeSentEventsFromQueue();

        // If saving fails now (or is already running and fails
        // later) then we may end up sending duplicate events
        // later. This will hopefully be very rare.
        savingTask.start();
    }

    private void removeSentEventsFromQueue() {

        synchronized (sendQueue) {
            assert eventsToRemoveAfterSend.intValue() <= sendQueue.size();

            while (eventsToRemoveAfterSend.intValue() > 0) {
                sendQueue.pop();
                eventsToRemoveAfterSend.decrementAndGet();
            }
        }
    }
}
