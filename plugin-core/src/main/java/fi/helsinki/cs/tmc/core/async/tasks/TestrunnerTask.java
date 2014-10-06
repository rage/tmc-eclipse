package fi.helsinki.cs.tmc.core.async.tasks;

import fi.helsinki.cs.tmc.core.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.domain.TestRunResult;

public abstract class TestrunnerTask extends BackgroundTask {

    public TestrunnerTask(final String description) {

        super(description);
    }

    public abstract TestRunResult get();
}
