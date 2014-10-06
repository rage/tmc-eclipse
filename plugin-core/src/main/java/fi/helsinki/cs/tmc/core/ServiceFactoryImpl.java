package fi.helsinki.cs.tmc.core;

import fi.helsinki.cs.tmc.core.async.tasks.SingletonTask;
import fi.helsinki.cs.tmc.core.io.FileIO;
import fi.helsinki.cs.tmc.core.io.IOFactory;
import fi.helsinki.cs.tmc.core.io.IOFactoryImpl;
import fi.helsinki.cs.tmc.core.services.CourseDAO;
import fi.helsinki.cs.tmc.core.services.DAOManager;
import fi.helsinki.cs.tmc.core.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.services.ProjectEventHandler;
import fi.helsinki.cs.tmc.core.services.ReviewDAO;
import fi.helsinki.cs.tmc.core.services.Settings;
import fi.helsinki.cs.tmc.core.services.Updater;
import fi.helsinki.cs.tmc.core.services.http.ServerManager;
import fi.helsinki.cs.tmc.core.spyware.SpywarePluginLayer;
import fi.helsinki.cs.tmc.core.spyware.async.SavingTask;
import fi.helsinki.cs.tmc.core.spyware.async.SendingTask;
import fi.helsinki.cs.tmc.core.spyware.services.DocumentChangeHandler;
import fi.helsinki.cs.tmc.core.spyware.services.EventSendBuffer;
import fi.helsinki.cs.tmc.core.spyware.services.EventStore;
import fi.helsinki.cs.tmc.core.spyware.services.LoggableEvent;
import fi.helsinki.cs.tmc.core.spyware.services.SnapshotTaker;
import fi.helsinki.cs.tmc.core.spyware.utility.ActiveThreadSet;

import java.util.ArrayDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

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
