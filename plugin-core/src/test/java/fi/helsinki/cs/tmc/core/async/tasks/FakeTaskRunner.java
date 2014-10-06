package fi.helsinki.cs.tmc.core.async.tasks;

import fi.helsinki.cs.tmc.core.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.async.BackgroundTaskListener;
import fi.helsinki.cs.tmc.core.async.BackgroundTaskRunner;
import fi.helsinki.cs.tmc.core.async.TaskStatusMonitor;

public class FakeTaskRunner implements BackgroundTaskRunner {

    class TaskStatusMonitorDummy implements TaskStatusMonitor {

        @Override
        public void startProgress(final String message, final int amountOfWork) {

        }

        @Override
        public void incrementProgress(final int progress) {

        }

        @Override
        public boolean isCancelRequested() {

            return false;
        }

    }

    private final TaskStatusMonitor taskFeedback;

    public FakeTaskRunner() {

        taskFeedback = new TaskStatusMonitorDummy();
    }

    @Override
    public void runTask(final BackgroundTask task) {

        task.start(taskFeedback);
    }

    @Override
    public void runTask(final BackgroundTask task, final BackgroundTaskListener listener) {

        listener.onBegin();

        final int returnValue = task.start(taskFeedback);

        if (returnValue == BackgroundTask.RETURN_FAILURE) {
            listener.onFailure();
        }
        if (returnValue == BackgroundTask.RETURN_SUCCESS) {
            listener.onSuccess();
        }
    }

    @Override
    public void cancelTask(final BackgroundTask task) {

    }

}
