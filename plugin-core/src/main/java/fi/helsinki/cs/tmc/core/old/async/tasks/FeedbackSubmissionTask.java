package fi.helsinki.cs.tmc.core.old.async.tasks;

import java.util.List;

import fi.helsinki.cs.tmc.core.old.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.old.async.TaskStatusMonitor;
import fi.helsinki.cs.tmc.core.old.domain.FeedbackAnswer;
import fi.helsinki.cs.tmc.core.old.services.FeedbackAnswerSubmitter;
import fi.helsinki.cs.tmc.core.old.ui.IdeUIInvoker;

/**
 * This is the background task for feedback submission.
 */
public class FeedbackSubmissionTask extends BackgroundTask {

    private TaskStatusMonitor progress;
    private final FeedbackAnswerSubmitter submitter;
    private final String feedbackUrl;
    private final List<FeedbackAnswer> answers;

    private final IdeUIInvoker invoker;

    /**
     *
     * @param submitter
     *            The actual object that handles feedback submission
     * @param answers
     *            List of answers to feedback questions
     * @param feedbackUrl
     *            url where the feedback is posted
     * @param invoker
     *            An ide-specific object that allows us to invoke ide ui from
     *            core (in this case, error messages)
     */
    public FeedbackSubmissionTask(final FeedbackAnswerSubmitter submitter, final List<FeedbackAnswer> answers, final String feedbackUrl,
            final IdeUIInvoker invoker) {

        super("Submitting feedback");

        this.submitter = submitter;
        this.answers = answers;
        this.feedbackUrl = feedbackUrl;
        this.invoker = invoker;
    }

    @Override
    public int start(final TaskStatusMonitor progress) {

        progress.startProgress(this.getDescription(), 1);

        try {

            submitter.submitFeedback(answers, feedbackUrl);
            progress.incrementProgress(1);

        } catch (final Exception ex) {

            invoker.raiseVisibleException("An error occured while submitting feedback:\n" + ex.getMessage());
            return BackgroundTask.RETURN_FAILURE;

        }

        return BackgroundTask.RETURN_SUCCESS;
    }

    @Override
    public void stop() {

        // we can't stop here, it's bat country
    }
}
