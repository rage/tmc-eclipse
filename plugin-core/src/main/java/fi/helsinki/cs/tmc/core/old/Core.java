package fi.helsinki.cs.tmc.core.old.old;

import fi.helsinki.cs.tmc.core.old.old.async.BackgroundTaskRunner;
import fi.helsinki.cs.tmc.core.old.old.io.IOFactory;
import fi.helsinki.cs.tmc.core.old.old.services.CourseDAO;
import fi.helsinki.cs.tmc.core.old.old.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.old.old.services.ProjectEventHandler;
import fi.helsinki.cs.tmc.core.old.old.services.ReviewDAO;
import fi.helsinki.cs.tmc.core.old.old.services.Settings;
import fi.helsinki.cs.tmc.core.old.old.services.Updater;
import fi.helsinki.cs.tmc.core.old.old.services.http.ServerManager;
import fi.helsinki.cs.tmc.core.old.old.spyware.SpywarePluginLayer;

/**
 * This class serves as an interface to the IDE plugin. None of these methods
 * should be called from core as this introduces annoying hidden dependencies
 * that make unit testing really really painful (trust me, been there, done
 * that, had to refactor code).
 */
public final class Core {

    private static Core core;

    private TMCErrorHandler errorHandler;
    private BackgroundTaskRunner taskRunner;
    private final Settings settings;
    private final SpywarePluginLayer spyware;

    private final CourseDAO courseDAO;
    private final ProjectDAO projectDAO;
    private final ReviewDAO reviewDAO;

    private final ServerManager server;

    private final Updater updater;

    private final ProjectEventHandler projectEventHandler;
    private final IOFactory io;

    private Core(final ServiceFactory factory) {

        settings = factory.getSettings();
        courseDAO = factory.getCourseDAO();
        projectDAO = factory.getProjectDAO();
        reviewDAO = factory.getReviewDAO();
        server = factory.getServerManager();
        updater = factory.getUpdater();
        errorHandler = new DummyErrorHandler();
        spyware = factory.getSpyware();
        projectEventHandler = factory.getProjectEventHandler();
        io = factory.getIOFactory();
    }

    public static void setErrorHandler(final TMCErrorHandler errorHandler) {

        final Core core = Core.getInstance();
        core.errorHandler = errorHandler;
    }

    public static TMCErrorHandler getErrorHandler() {

        return Core.getInstance().errorHandler;
    }

    public static void setTaskRunner(final BackgroundTaskRunner taskRunner) {

        final Core core = Core.getInstance();
        core.taskRunner = taskRunner;
    }

    public static BackgroundTaskRunner getTaskRunner() {

        return Core.getInstance().taskRunner;
    }

    public static Settings getSettings() {

        return Core.getInstance().settings;
    }

    public static CourseDAO getCourseDAO() {

        return Core.getInstance().courseDAO;
    }

    public static ProjectDAO getProjectDAO() {

        return Core.getInstance().projectDAO;
    }

    public static ReviewDAO getReviewDAO() {

        return Core.getInstance().reviewDAO;
    }

    public static ServerManager getServerManager() {

        return Core.getInstance().server;
    }

    public static Updater getUpdater() {

        return Core.getInstance().updater;
    }

    public static SpywarePluginLayer getSpyware() {

        return Core.getInstance().spyware;
    }

    public static ProjectEventHandler getProjectEventHandler() {

        return Core.getInstance().projectEventHandler;
    }

    public static IOFactory getIOFactory() {

        return Core.getInstance().io;
    }

    public static Core getInstance() {

        if (core == null) {
            return getInstance(new ServiceFactoryImpl());
        }

        return core;
    }

    public static Core getInstance(final ServiceFactory factory) {

        if (core == null) {
            core = new Core(factory);
        }

        return core;
    }
}
