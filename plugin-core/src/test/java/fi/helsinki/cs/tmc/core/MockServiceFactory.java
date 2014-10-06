package fi.helsinki.cs.tmc.core;

import fi.helsinki.cs.tmc.core.io.IOFactory;
import fi.helsinki.cs.tmc.core.services.CourseDAO;
import fi.helsinki.cs.tmc.core.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.services.ProjectEventHandler;
import fi.helsinki.cs.tmc.core.services.ReviewDAO;
import fi.helsinki.cs.tmc.core.services.Settings;
import fi.helsinki.cs.tmc.core.services.Updater;
import fi.helsinki.cs.tmc.core.services.http.ServerManager;
import fi.helsinki.cs.tmc.core.spyware.SpywarePluginLayer;

import static org.mockito.Mockito.mock;

public class MockServiceFactory implements ServiceFactory {

    private final Settings settings;
    private final CourseDAO courseDAO;
    private final ProjectDAO projectDAO;
    private final ReviewDAO reviewDAO;
    private final ServerManager serverManager;
    private final Updater updater;
    private final SpywarePluginLayer spywarePluginLayer;
    private final ProjectEventHandler eventHandler;
    private final IOFactory io;

    public MockServiceFactory() {

        settings = mock(Settings.class);
        courseDAO = mock(CourseDAO.class);
        projectDAO = mock(ProjectDAO.class);
        reviewDAO = mock(ReviewDAO.class);
        serverManager = mock(ServerManager.class);
        updater = mock(Updater.class);
        spywarePluginLayer = mock(SpywarePluginLayer.class);
        eventHandler = mock(ProjectEventHandler.class);
        io = mock(IOFactory.class);
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
    public ServerManager getServerManager() {

        return serverManager;
    }

    @Override
    public Updater getUpdater() {

        return updater;
    }

    @Override
    public SpywarePluginLayer getSpyware() {

        return spywarePluginLayer;
    }

    @Override
    public ProjectEventHandler getProjectEventHandler() {

        return eventHandler;
    }

    @Override
    public ReviewDAO getReviewDAO() {

        return reviewDAO;
    }

    @Override
    public IOFactory getIOFactory() {

        return io;
    }

}
