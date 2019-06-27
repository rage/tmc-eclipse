package fi.helsinki.cs.tmc.core.old.async.tasks;

import java.io.IOException;
import java.util.List;

import fi.helsinki.cs.tmc.core.old.async.SimpleBackgroundTask;
import fi.helsinki.cs.tmc.core.old.domain.Exercise;
import fi.helsinki.cs.tmc.core.old.domain.Project;
import fi.helsinki.cs.tmc.core.old.domain.ProjectStatus;
import fi.helsinki.cs.tmc.core.old.domain.ZippedProject;
import fi.helsinki.cs.tmc.core.old.io.FileIO;
import fi.helsinki.cs.tmc.core.old.io.FileUtil;
import fi.helsinki.cs.tmc.core.old.io.IOFactory;
import fi.helsinki.cs.tmc.core.old.io.zip.Unzipper;
import fi.helsinki.cs.tmc.core.old.io.zip.unzippingdecider.UnzippingDeciderFactory;
import fi.helsinki.cs.tmc.core.old.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.old.services.ProjectDownloader;
import fi.helsinki.cs.tmc.core.old.services.ProjectOpener;
import fi.helsinki.cs.tmc.core.old.services.Settings;
import fi.helsinki.cs.tmc.core.old.ui.IdeUIInvoker;

/**
 * This task downloads and unzips projects and opens them in IDE. Like all
 * background tasks, it is run as an asynchronous task to prevent IDE from
 * freezing while the exercises are being updated.
 */
public class DownloaderTask extends SimpleBackgroundTask<Exercise> {

    private final ProjectDAO projectDao;
    private final Settings settings;
    private final ProjectDownloader downloader;
    private final ProjectOpener opener;
    private final IdeUIInvoker invoker;
    private final IOFactory io;

    /**
     *
     * @param downloader
     *            Object that handles the actual download
     * @param opener
     *            Object that handles the project opening in the IDE. Requires
     *            IDE-specific implementation
     * @param exercises
     *            List of exercises to be downloaded. Exercise-objects contain
     *            necessary URLs etc for download
     * @param projectDao
     *            DAO that handles project storage.
     * @param settings
     *            Settings-object. Required for settings (duh)
     * @param invoker
     *            IDE UI invoker. Required for ability to show error messages.
     *            Requires IDE-specific implementation
     */
    public DownloaderTask(final ProjectDownloader downloader, final ProjectOpener opener, final List<Exercise> exercises,
            final ProjectDAO projectDao, final Settings settings, final IdeUIInvoker invoker, final IOFactory io) {

        super("Downloading exercises", exercises);

        this.settings = settings;
        this.downloader = downloader;
        this.opener = opener;
        this.projectDao = projectDao;
        this.invoker = invoker;
        this.io = io;
    }

    /**
     * Handles single exercise downloading, unzipping and opening. This method
     * is called by the SimpleBackgroundTask super class
     */
    @Override
    public void run(final Exercise exercise) {

        try {
            exercise.setUpdateAvailable(false);

            Project project = projectDao.getProjectByExercise(exercise);

            final ZippedProject zip = downloader.downloadExercise(exercise);
            final UnzippingDeciderFactory factory = new UnzippingDeciderFactory(io);
            final Unzipper unzipper = new Unzipper(zip, factory.createUnzippingDecider(project));
            final FileIO folder = new FileIO(FileUtil.append(settings.getExerciseFilePath(), settings.getCurrentCourseName()));
            final List<String> fileList = unzipper.unzipTo(folder);

            if (project == null) {
                project = new Project(exercise, fileList);
                projectDao.addProject(project);
            } else {
                project.setProjectFiles(fileList);
            }

            project.setStatus(ProjectStatus.DOWNLOADED);

            opener.open(exercise);
        } catch (final IOException exception) {
            invoker.raiseVisibleException("An error occurred while unzipping the exercises");
            exercise.setUpdateAvailable(true);
        }
    }
}
