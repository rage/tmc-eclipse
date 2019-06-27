package fi.helsinki.cs.tmc.core.old.async.tasks;

import fi.helsinki.cs.tmc.core.old.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.old.async.TaskStatusMonitor;
import fi.helsinki.cs.tmc.core.old.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.old.services.ProjectUploader;
import fi.helsinki.cs.tmc.core.old.ui.IdeUIInvoker;

/**
 * Background task for sending projects to the TestMyCode pastebin. It is
 * closely related to the uploading task as the only real difference is single
 * parameter in the HTTP request (potentially merge to single task? Constructor
 * parameter list would be nasty though).
 */
public class PastebinTask extends BackgroundTask {

    private final ProjectUploader uploader;
    private final String path;
    private final String pasteMessage;

    private final ProjectDAO projectDAO;
    private final IdeUIInvoker invoker;

    public PastebinTask(final ProjectUploader uploader,
                        final String path,
                        final String pasteMessage,
                        final ProjectDAO projectDAO,
                        final IdeUIInvoker invoker) {

        super("Creating a pastebin");
        this.uploader = uploader;
        this.path = path;
        this.pasteMessage = pasteMessage;

        this.projectDAO = projectDAO;
        this.invoker = invoker;
    }

    @Override
    public int start(final TaskStatusMonitor progress) {

        progress.startProgress(this.getDescription(), 2);

        try {
            uploader.setProject(projectDAO.getProjectByFile(path));
            uploader.setAsPaste(pasteMessage);
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

        } catch (final Exception ex) {
            invoker.raiseVisibleException("An error occurred while uploading exercise to pastebin:\n" + ex.getMessage());
            return BackgroundTask.RETURN_FAILURE;
        }

        return BackgroundTask.RETURN_SUCCESS;

    }

    public String getPasteUrl() {

        return uploader.getResponse().getPasteUrl().toString();
    }
}
