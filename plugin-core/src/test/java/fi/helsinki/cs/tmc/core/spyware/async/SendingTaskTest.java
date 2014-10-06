package fi.helsinki.cs.tmc.core.spyware.async;

import fi.helsinki.cs.tmc.core.async.tasks.SingletonTask;
import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.services.CourseDAO;
import fi.helsinki.cs.tmc.core.services.Settings;
import fi.helsinki.cs.tmc.core.services.http.ServerManager;
import fi.helsinki.cs.tmc.core.spyware.services.EventStore;
import fi.helsinki.cs.tmc.core.spyware.services.LoggableEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SendingTaskTest {

    private SendingTask task;
    private ArrayDeque<LoggableEvent> sendQueue;
    private ServerManager serverManager;
    private CourseDAO courseDAO;
    private Settings settings;
    private SingletonTask savingTask;
    private EventStore eventStore;
    private AtomicInteger eventsToRemoveAfterSend;

    @Before
    public void setUp() throws Exception {

        initializeSendQueue();
        initializeServerManager();
        initializeSettings();
        initializeCourseDAO();
        initializeEventStore();

        eventsToRemoveAfterSend = new AtomicInteger();
        savingTask = new SingletonTask(new SavingTask(sendQueue, eventStore), Executors.newScheduledThreadPool(2));
        task = new SendingTask(sendQueue, serverManager, courseDAO, settings, savingTask, eventsToRemoveAfterSend);
    }

    @Test
    public void runWhenThereIsNoUrlsTest() throws InterruptedException {

        task.run();
        assertEquals(sendQueue.size(), 5);
    }

    @Test
    public void runWhenSpywareIsDisabledTest() throws InterruptedException {

        when(settings.isSpywareEnabled()).thenReturn(false);

        for (final Course c : courseDAO.getCourses()) {
            final List<String> l = new ArrayList<String>();
            l.add("a");
            l.add("b");
            l.add("c");
            c.setSpywareUrls(l);
        }

        task.run();

        assertEquals(sendQueue.size(), 5);
    }

    @Test
    public void runTest() {

        for (final Course c : courseDAO.getCourses()) {
            final List<String> l = new ArrayList<String>();
            l.add("a");
            l.add("b");
            l.add("c");
            c.setSpywareUrls(l);
        }

        task.run();

        assertEquals(0, eventsToRemoveAfterSend.intValue());
    }

    private void initializeEventStore() {

        eventStore = mock(EventStore.class);
    }

    private void initializeCourseDAO() {

        courseDAO = mock(CourseDAO.class);
        final List<Course> l = new ArrayList<Course>();
        l.add(new Course("Course1"));
        l.add(new Course("Course2"));
        l.add(new Course("Course3"));
        l.add(new Course("Course4"));
        l.add(new Course("Course5"));

        when(courseDAO.getCourses()).thenReturn(l);
        when(courseDAO.getCurrentCourse(any(Settings.class))).thenReturn(l.get(1));
    }

    private void initializeServerManager() {

        serverManager = mock(ServerManager.class);
    }

    private void initializeSettings() {

        settings = mock(Settings.class);
        when(settings.isSpywareEnabled()).thenReturn(true);
    }

    private void initializeSendQueue() {

        sendQueue = new ArrayDeque<LoggableEvent>();
        sendQueue.add(new LoggableEvent("course1", "exercise1", "file_change", new byte[10], "metadata1"));
        sendQueue.add(new LoggableEvent("course2", "exercise2", "file_change", new byte[10], "metadata2"));
        sendQueue.add(new LoggableEvent("course3", "exercise3", "file_change", new byte[10], "metadata3"));
        sendQueue.add(new LoggableEvent("course4", "exercise4", "file_change", new byte[10], "metadata4"));
        sendQueue.add(new LoggableEvent("course5", "exercise5", "file_change", new byte[10], "metadata5"));
    }
}
