package fi.helsinki.cs.tmc.core.async.listeners;

import fi.helsinki.cs.tmc.core.async.BackgroundTaskListener;
import fi.helsinki.cs.tmc.core.async.tasks.FetchCodeReviewsTask;
import fi.helsinki.cs.tmc.core.domain.Review;
import fi.helsinki.cs.tmc.core.services.ReviewDAO;
import fi.helsinki.cs.tmc.core.ui.IdeUIInvoker;

import java.util.List;

public class FetchCodeReviewsTaskListener implements BackgroundTaskListener {

    private final FetchCodeReviewsTask task;
    private final IdeUIInvoker invoker;
    private final ReviewDAO reviewDAO;
    private final boolean showMessages;

    public FetchCodeReviewsTaskListener(final FetchCodeReviewsTask task, final IdeUIInvoker invoker, final ReviewDAO reviewDAO,
            final boolean showMessages) {

        this.task = task;
        this.invoker = invoker;
        this.reviewDAO = reviewDAO;
        this.showMessages = showMessages;
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess() {

        final List<Review> unseen = reviewDAO.unseen();

        if (unseen.isEmpty() && showMessages) {
            invoker.invokeMessageBox("No new code reviews.");
            return;
        } else if (!unseen.isEmpty() && !showMessages) {
            invoker.invokeCodeReviewPopupNotification(unseen);
        } else {
            for (final Review reviewr : unseen) {
                reviewr.setMarkedAsRead(true);
                invoker.invokeCodeReviewDialog(reviewr);
            }
        }
    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onInterruption() {

    }

}
