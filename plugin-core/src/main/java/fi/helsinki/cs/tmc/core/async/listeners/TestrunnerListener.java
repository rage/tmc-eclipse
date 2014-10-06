package fi.helsinki.cs.tmc.core.async.listeners;

import fi.helsinki.cs.tmc.core.async.BackgroundTaskListener;
import fi.helsinki.cs.tmc.core.async.tasks.TestrunnerTask;
import fi.helsinki.cs.tmc.core.domain.TestCaseResult;
import fi.helsinki.cs.tmc.core.domain.TestRunResult;
import fi.helsinki.cs.tmc.core.ui.IdeUIInvoker;

import java.util.List;

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
