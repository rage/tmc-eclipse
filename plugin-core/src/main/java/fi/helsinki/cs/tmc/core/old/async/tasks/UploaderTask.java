package fi.helsinki.cs.tmc.core.old.async.tasks;

import fi.helsinki.cs.tmc.core.old.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.old.async.StopStatus;
import fi.helsinki.cs.tmc.core.old.async.TaskStatusMonitor;
import fi.helsinki.cs.tmc.core.old.domain.Project;
import fi.helsinki.cs.tmc.core.old.domain.SubmissionResult;
import fi.helsinki.cs.tmc.core.old.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.old.services.ProjectUploader;
import fi.helsinki.cs.tmc.core.old.ui.IdeUIInvoker;

/**
 * Background task for handling the file uploads to server. Used when submitting
 * files to server for grading.
 */
public class UploaderTask extends BackgroundTask {

    private final ProjectUploader uploader;
    private final String path;

    private final ProjectDAO projectDAO;
    private final IdeUIInvoker invoker;

    public UploaderTask(final ProjectUploader uploader, final String path, final ProjectDAO projectDAO, final IdeUIInvoker invoker) {

        super("Uploading exercises");
        this.uploader = uploader;
        this.path = path;
        this.projectDAO = projectDAO;
        this.invoker = invoker;
    }

    @Override
    public int start(final TaskStatusMonitor progress) {

        progress.startProgress(this.getDescription(), 3);

        return run(progress);

    }

    private int run(final TaskStatusMonitor progress) {

        try {
            uploader.setProject(projectDAO.getProjectByFile(path));
            uploader.zipProjects();

            if (shouldStop(progress)) {
                return BackgroundTask.RETURN_INTERRUPTED;
            }

            progress.incrementProgress(1);

            uploader.handleSubmissionResponse();
            if (shouldStop(progress)) {
                return BackgroundTask.RETURN_INTERRUPTED;
            }
            progress.incrementProgress(1);

            uploader.handleSubmissionResult(new StopStatus() {

                @Override
                public boolean mustStop() {

                    return shouldStop(progress);
                }
            });

            if (getResult() == null) {
                return BackgroundTask.RETURN_FAILURE;
            }

            progress.incrementProgress(1);

        } catch (final Exception ex) {
            invoker.raiseVisibleException("An error occurred while uploading exercises:\n" + ex.getMessage());
            return BackgroundTask.RETURN_FAILURE;
        }

        return BackgroundTask.RETURN_SUCCESS;

    }

    public SubmissionResult getResult() {

        return uploader.getResult();
    }

    public Project getProject() {

        return uploader.getProject();
    }
}
