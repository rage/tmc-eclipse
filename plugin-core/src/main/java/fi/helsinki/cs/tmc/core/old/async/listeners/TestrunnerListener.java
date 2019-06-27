package fi.helsinki.cs.tmc.core.old.old.async.listeners;

import java.util.List;

import fi.helsinki.cs.tmc.core.old.old.async.BackgroundTaskListener;
import fi.helsinki.cs.tmc.core.old.old.async.tasks.TestrunnerTask;
import fi.helsinki.cs.tmc.core.old.old.domain.TestCaseResult;
import fi.helsinki.cs.tmc.core.old.old.domain.TestRunResult;
import fi.helsinki.cs.tmc.core.old.old.ui.IdeUIInvoker;

public class TestrunnerListener implements BackgroundTaskListener {

    private final TestrunnerTask task;
    private final IdeUIInvoker uiInvoker;

    public TestrunnerListener(final TestrunnerTask task, final IdeUIInvoker uiInvoker) {

        this.task = task;
        this.uiInvoker = uiInvoker;
    }

    @Override
    public void onBegin() {

        // TODO: Popout "Running tests"
    }

    @Override
    public void onSuccess() {

        final TestRunResult result = task.get();

        uiInvoker.invokeTestResultWindow(result.getTestCaseResults());

        if (allPassed(result.getTestCaseResults())) {
            uiInvoker.invokeSubmitToServerWindow();
        }
    }

    private boolean allPassed(final List<TestCaseResult> testCaseResults) {

        for (final TestCaseResult result : testCaseResults) {
            if (!result.isSuccessful()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onFailure() {

        // TODO: Kill "Running tests"
        // TODO: Popout error
    }

    @Override
    public void onInterruption() {

    }

}
