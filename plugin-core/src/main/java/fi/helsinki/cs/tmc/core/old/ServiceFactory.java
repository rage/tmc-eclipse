package fi.helsinki.cs.tmc.core.old.old;

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
