package fi.helsinki.cs.tmc.core.async.tasks;

import fi.helsinki.cs.tmc.core.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.async.TaskStatusMonitor;
import fi.helsinki.cs.tmc.core.domain.exception.InvalidProjectException;
import fi.helsinki.cs.tmc.core.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.services.ProjectUploader;
import fi.helsinki.cs.tmc.core.ui.IdeUIInvoker;

import java.io.IOException;

/**
 * Background task responsible for handling code review requests.
 */
public class CodeReviewRequestTask extends BackgroundTask {

    private final ProjectUploader uploader;
    private final String path;
    private final String requestMessage;

    private final ProjectDAO projectDAO;
    private final IdeUIInvoker invoker;

    /**
     *
     * @param uploader
     *            Object that handles project upload. Since project upload and
     *            code review are so similar (both involve uploading project),
     *            the object is used here as well
     * @param path
     *            Path to project
     * @param requestMessage
     *            User code review message
     * @param projectDAO
     *            Object that contains all the projects
     * @param invoker
     *            Ide specific object that allows us to invoke ide UI (error
     *            messages for example)
     */
    public CodeReviewRequestTask(final ProjectUploader uploader, final String path, final String requestMessage, final ProjectDAO projectDAO,
            final IdeUIInvoker invoker) {

        super("Creating code review request");

        this.uploader = uploader;
        this.path = path;
        this.requestMessage = requestMessage;
        this.projectDAO = projectDAO;
        this.invoker = invoker;
    }

    @Override
    public int start(final TaskStatusMonitor progress) {

        progress.startProgress(this.getDescription(), 2);
        return run(progress);
    }

    private int run(final TaskStatusMonitor progress) {

        try {
            uploader.setProject(projectDAO.getProjectByFile(path));
            uploader.setAsRequest(requestMessage);
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

        } catch (final InvalidProjectException | IOException exception) {
            invoker.raiseVisibleException("An error occurred while requesting code review:\n" + exception.getMessage());
            return BackgroundTask.RETURN_FAILURE;
        }

        return BackgroundTask.RETURN_SUCCESS;

    }
}
