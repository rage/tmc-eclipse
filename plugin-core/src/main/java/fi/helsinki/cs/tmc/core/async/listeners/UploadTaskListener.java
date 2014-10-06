package fi.helsinki.cs.tmc.core.async.listeners;

import fi.helsinki.cs.tmc.core.async.BackgroundTaskListener;
import fi.helsinki.cs.tmc.core.async.tasks.UploaderTask;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.SubmissionResult;
import fi.helsinki.cs.tmc.core.ui.IdeUIInvoker;
import fi.helsinki.cs.tmc.core.utils.ProjectIconHandler;

public class UploadTaskListener implements BackgroundTaskListener {

    private final UploaderTask task;
    private final IdeUIInvoker uiInvoker;
    private final ProjectIconHandler projectIconHandler;

    public UploadTaskListener(final UploaderTask task, final IdeUIInvoker uiInvoker, final ProjectIconHandler projectIconHandler) {

        this.task = task;
        this.uiInvoker = uiInvoker;
        this.projectIconHandler = projectIconHandler;
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess() {

        final SubmissionResult result = task.getResult();

        if (result == null) {
            return;
        }

        final String exerciseName = task.getProject().getExercise().getName();
        final Exercise exercise = task.getProject().getExercise();

        uiInvoker.invokeTestResultWindow(result.getTestCases());

        if (result.allTestCasesSucceeded()) {

            uiInvoker.invokeAllTestsPassedWindow(result, exerciseName);
            exercise.setCompleted(true);

        } else if (result.allTestCasesFailed()) {

            uiInvoker.invokeAllTestsFailedWindow(result, exerciseName);
            exercise.setAttempted(true);

        } else {

            uiInvoker.invokeSomeTestsFailedWindow(result, exerciseName);
            exercise.setAttempted(true);

        }

        projectIconHandler.updateIcon(exercise);
    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onInterruption() {

    }
}
