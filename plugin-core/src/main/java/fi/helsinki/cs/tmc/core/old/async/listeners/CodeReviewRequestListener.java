package fi.helsinki.cs.tmc.core.old.old.async.listeners;

import fi.helsinki.cs.tmc.core.old.old.async.BackgroundTaskListener;
import fi.helsinki.cs.tmc.core.old.old.async.tasks.CodeReviewRequestTask;
import fi.helsinki.cs.tmc.core.old.old.ui.IdeUIInvoker;

public class CodeReviewRequestListener implements BackgroundTaskListener {

    private final CodeReviewRequestTask task;
    private final IdeUIInvoker uiInvoker;

    public CodeReviewRequestListener(final CodeReviewRequestTask task, final IdeUIInvoker uiInvoker) {

        this.task = task;
        this.uiInvoker = uiInvoker;
    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onSuccess() {

        uiInvoker.invokeCodeReviewRequestSuccefullySentWindow();
    }

    @Override
    public void onFailure() {

        uiInvoker.raiseVisibleException("Failed to create the code review request.");
    }

    @Override
    public void onInterruption() {

    }
}
