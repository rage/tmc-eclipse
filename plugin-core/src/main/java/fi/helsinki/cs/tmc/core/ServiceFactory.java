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

/**
 * An interface that all the service factories implement. Handles the creation
 * of various services for the Core. Makes unit testing easier as it's possible
 * to provide mock implementations of factories and avoid unnecessary
 * dependencies to, for example, file system.
 */
public interface ServiceFactory {

    Settings getSettings();

    CourseDAO getCourseDAO();

    ProjectDAO getProjectDAO();

    ReviewDAO getReviewDAO();

    ServerManager getServerManager();

    Updater getUpdater();

    SpywarePluginLayer getSpyware();

    ProjectEventHandler getProjectEventHandler();

    IOFactory getIOFactory();

}
