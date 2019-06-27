package fi.helsinki.cs.tmc.core.old.old.async.tasks;

import fi.helsinki.cs.tmc.core.old.old.async.BackgroundTask;
import fi.helsinki.cs.tmc.core.old.old.domain.TestRunResult;

public abstract class TestrunnerTask extends BackgroundTask {

    public TestrunnerTask(final String description) {

        super(description);
    }

    public abstract TestRunResult get();
}
