package fi.helsinki.cs.tmc.core.async.tasks;

import fi.helsinki.cs.tmc.core.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.async.TaskStatusMonitor;
import fi.helsinki.cs.tmc.core.domain.Review;
import fi.helsinki.cs.tmc.core.services.http.ServerManager;

public class MarkReviewAsReadTask extends BackgroundTask {

    private final ServerManager server;
    private final Review review;

    public MarkReviewAsReadTask(final ServerManager server, final Review review) {

        super("Marking review as read");
        this.server = server;
        this.review = review;
    }

    @Override
    public int start(final TaskStatusMonitor progress) {

        progress.startProgress(this.getDescription(), 1);

        final boolean success = server.markReviewAsRead(review);
        progress.incrementProgress(1);

        return success ? RETURN_SUCCESS : RETURN_FAILURE;
    }
}
