package fi.helsinki.cs.tmc.core.old;

import fi.helsinki.cs.tmc.core.old.io.IOFactory;
import fi.helsinki.cs.tmc.core.old.services.CourseDAO;
import fi.helsinki.cs.tmc.core.old.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.old.services.ProjectEventHandler;
import fi.helsinki.cs.tmc.core.old.services.ReviewDAO;
import fi.helsinki.cs.tmc.core.old.services.Settings;
import fi.helsinki.cs.tmc.core.old.services.Updater;
import fi.helsinki.cs.tmc.core.old.services.http.ServerManager;
import fi.helsinki.cs.tmc.core.old.spyware.SpywarePluginLayer;

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
