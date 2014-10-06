package fi.helsinki.cs.tmc.core.domain;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * Class that stores the submission result that the server provides us after
 * submitting exercises; for example which tests failed on server and the
 * solution URL.
 */
public class SubmissionResult {

    public static enum Status {
        OK, FAIL, ERROR, PROCESSING
    }

    @SerializedName("status")
    private Status status;

    @SerializedName("error")
    private String error;

    @SerializedName("test_cases")
    private List<TestCaseResult> testCases;

    @SerializedName("solution_url")
    private String solutionUrl;

    @SerializedName("points")
    private List<String> points;

    @SerializedName("missing_review_points")
    private List<String> missingReviewPoints;

    @SerializedName("feedback_questions")
    private List<FeedbackQuestion> feedbackQuestions;

    @SerializedName("feedback_answer_url")
    private String feedbackAnswerUrl;

    public SubmissionResult() {

        status = Status.ERROR;
        error = null;
        testCases = Collections.emptyList();
        points = Collections.emptyList();
        missingReviewPoints = Collections.emptyList();
        feedbackQuestions = Collections.emptyList();
    }

    public Status getStatus() {

        return status;
    }

    public void setStatus(final Status status) {

        this.status = status;
    }

    public String getError() {

        return error;
    }

    public void setError(final String error) {

        this.error = error;
    }

    public List<TestCaseResult> getTestCases() {

        return testCases;
    }

    public void setTestCases(final List<TestCaseResult> testCases) {

        this.testCases = testCases;
    }

    public String getSolutionUrl() {

        return solutionUrl;
    }

    public void setSolutionUrl(final String solutionUrl) {

        this.solutionUrl = solutionUrl;
    }

    public List<String> getPoints() {

        return points;
    }

    public void setPoints(final List<String> points) {

        this.points = points;
    }

    public List<String> getMissingReviewPoints() {

        return missingReviewPoints;
    }

    public void setMissingReviewPoints(final List<String> missingReviewPoints) {

        this.missingReviewPoints = missingReviewPoints;
    }

    public List<FeedbackQuestion> getFeedbackQuestions() {

        return feedbackQuestions;
    }

    public void setFeedbackQuestions(final List<FeedbackQuestion> feedbackQuestions) {

        this.feedbackQuestions = feedbackQuestions;
    }

    public String getFeedbackAnswerUrl() {

        return feedbackAnswerUrl;
    }

    public void setFeedbackAnswerUrl(final String feedbackAnswerUrl) {

        this.feedbackAnswerUrl = feedbackAnswerUrl;
    }

    public boolean allTestCasesFailed() {

        for (final TestCaseResult tcr : testCases) {
            if (tcr.isSuccessful()) {
                return false;
            }
        }
        return true;
    }

    public boolean allTestCasesSucceeded() {

        for (final TestCaseResult tcr : testCases) {
            if (!tcr.isSuccessful()) {
                return false;
            }
        }
        return true;
    }
}
