package fi.helsinki.cs.tmc.core.old.async.tasks;

import fi.helsinki.cs.tmc.core.old.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.old.domain.TestRunResult;

public abstract class TestrunnerTask extends BackgroundTask {

    public TestrunnerTask(final String description) {

        super(description);
    }

    public abstract TestRunResult get();
}
