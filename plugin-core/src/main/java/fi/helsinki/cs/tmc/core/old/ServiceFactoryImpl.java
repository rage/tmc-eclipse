package fi.helsinki.cs.tmc.core.old.old;

import java.util.ArrayDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import fi.helsinki.cs.tmc.core.old.old.async.tasks.SingletonTask;
import fi.helsinki.cs.tmc.core.old.old.io.FileIO;
import fi.helsinki.cs.tmc.core.old.old.io.IOFactory;
import fi.helsinki.cs.tmc.core.old.old.io.IOFactoryImpl;
import fi.helsinki.cs.tmc.core.old.old.services.CourseDAO;
import fi.helsinki.cs.tmc.core.old.old.services.DAOManager;
import fi.helsinki.cs.tmc.core.old.old.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.old.old.services.ProjectEventHandler;
import fi.helsinki.cs.tmc.core.old.old.services.ReviewDAO;
import fi.helsinki.cs.tmc.core.old.old.services.Settings;
import fi.helsinki.cs.tmc.core.old.old.services.Updater;
import fi.helsinki.cs.tmc.core.old.old.services.http.ServerManager;
import fi.helsinki.cs.tmc.core.old.old.spyware.SpywarePluginLayer;
import fi.helsinki.cs.tmc.core.old.old.spyware.async.SavingTask;
import fi.helsinki.cs.tmc.core.old.old.spyware.async.SendingTask;
import fi.helsinki.cs.tmc.core.old.old.spyware.services.DocumentChangeHandler;
import fi.helsinki.cs.tmc.core.old.old.spyware.services.EventSendBuffer;
import fi.helsinki.cs.tmc.core.old.old.spyware.services.EventStore;
import fi.helsinki.cs.tmc.core.old.old.spyware.services.LoggableEvent;
import fi.helsinki.cs.tmc.core.old.old.spyware.services.SnapshotTaker;
import fi.helsinki.cs.tmc.core.old.old.spyware.utility.ActiveThreadSet;

/**
 * Default implementation of ServiceFactory interface. Creates the various
 * services that Core uses.
 */
public final class ServiceFactoryImpl implements ServiceFactory {

    private final Settings settings;
    private final CourseDAO courseDAO;
    private final ProjectDAO projectDAO;
    private final ReviewDAO reviewDAO;

    private final ServerManager server;
    private final Updater updater;
    private final SpywarePluginLayer spyware;
    private final ProjectEventHandler projectEventHandler;

    private final IOFactory io;

    public ServiceFactoryImpl() {

        settings = Settings.getDefaultSettings();
        server = new ServerManager(settings);

        io = new IOFactoryImpl();

        final DAOManager manager = new DAOManager();
        courseDAO = manager.getCourseDAO();
        projectDAO = manager.getProjectDAO();
        reviewDAO = manager.getReviewDAO();

        updater = new Updater(server, courseDAO, projectDAO);
        projectEventHandler = new ProjectEventHandler(projectDAO);

        final AtomicInteger eventsToRemoveAfterSend = new AtomicInteger();
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        final ArrayDeque<LoggableEvent> sendQueue = new ArrayDeque<LoggableEvent>();
        final EventStore eventStore = new EventStore(new FileIO("events.tmp"));

        final SingletonTask savingTask = new SingletonTask(new SavingTask(sendQueue, eventStore), scheduler);
        final SingletonTask sendingTask = new SingletonTask(new SendingTask(sendQueue, server, courseDAO, settings, savingTask,
                eventsToRemoveAfterSend), scheduler);

        final EventSendBuffer receiver = new EventSendBuffer(eventStore, settings, sendQueue, sendingTask, savingTask, eventsToRemoveAfterSend);

        final ActiveThreadSet set = new ActiveThreadSet();
        final SnapshotTaker taker = new SnapshotTaker(set, receiver, settings, projectDAO);
        final DocumentChangeHandler handler = new DocumentChangeHandler(receiver, set, settings, projectDAO);

        spyware = new SpywarePluginLayer(set, receiver, taker, handler);
    }

    @Override
    public Settings getSettings() {

        return settings;
    }

    @Override
    public CourseDAO getCourseDAO() {

        return courseDAO;
    }

    @Override
    public ProjectDAO getProjectDAO() {

        return projectDAO;
    }

    @Override
    public ReviewDAO getReviewDAO() {

        return reviewDAO;
    }

    @Override
    public ServerManager getServerManager() {

        return server;
    }

    @Override
    public Updater getUpdater() {

        return updater;
    }

    @Override
    public SpywarePluginLayer getSpyware() {

        return spyware;
    }

    @Override
    public ProjectEventHandler getProjectEventHandler() {

        return projectEventHandler;
    }

    @Override
    public IOFactory getIOFactory() {

        return io;
    }

}
