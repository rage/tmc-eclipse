package fi.helsinki.cs.tmc.core.old.async.tasks;

import java.util.List;

import fi.helsinki.cs.tmc.core.old.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.old.async.TaskStatusMonitor;
import fi.helsinki.cs.tmc.core.old.domain.Course;
import fi.helsinki.cs.tmc.core.old.domain.Review;
import fi.helsinki.cs.tmc.core.old.services.ReviewDAO;
import fi.helsinki.cs.tmc.core.old.services.http.ServerManager;

public class FetchCodeReviewsTask extends BackgroundTask {

    private final Course course;
    private final ServerManager server;
    private final ReviewDAO reviewDAO;

    private List<Review> reviews;

    public FetchCodeReviewsTask(final Course course, final ServerManager server, final ReviewDAO reviewDAO) {

        super("Checking for new code reviews");
        this.course = course;
        this.server = server;
        this.reviewDAO = reviewDAO;
    }

    @Override
    public int start(final TaskStatusMonitor progress) {

        progress.startProgress(this.getDescription(), 2);
        reviews = server.downloadReviews(course);
        progress.incrementProgress(1);

        if (reviews == null) {
            return RETURN_FAILURE;
        }
        if (shouldStop(progress)) {
            return BackgroundTask.RETURN_INTERRUPTED;
        }

        reviewDAO.addAll(reviews);
        progress.incrementProgress(1);
        return RETURN_SUCCESS;
    }
}
