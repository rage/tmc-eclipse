package fi.helsinki.cs.plugin.tmc.async.tasks;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.plugin.tmc.async.BackgroundTask;
import fi.helsinki.cs.plugin.tmc.async.BackgroundTaskListener;
import fi.helsinki.cs.plugin.tmc.domain.Project;

public class MavenTestrunnerTaskTest {

    class MavenTestrunnerTaskSuccesful extends MavenTestrunnerTask {

        public MavenTestrunnerTaskSuccesful(Project project) {
            super(project);
        }

        @Override
        public int runMaven(List<String> goals, Project project) {
            return 0;
        }

    }

    class MavenTestrunnerTaskFailing extends MavenTestrunnerTask {

        public MavenTestrunnerTaskFailing(Project project) {
            super(project);
        }

        @Override
        public int runMaven(List<String> goals, Project project) {
            return 1;
        }

    }

    private TestTaskRunner taskRunner;
    private Project project;

    private MavenTestrunnerTask succesfulRunner;
    private MavenTestrunnerTask failingRunner;

    @Before
    public void setUp() {
        this.taskRunner = new TestTaskRunner();
        this.project = mock(Project.class);
        this.succesfulRunner = new MavenTestrunnerTaskSuccesful(project);
        this.failingRunner = new MavenTestrunnerTaskFailing(project);
    }

    @Test
    public void testImplementsInterfaces() {
        assertTrue(succesfulRunner instanceof BackgroundTask && succesfulRunner instanceof TestrunnerTask);
    }

    @Test
    public void testDescriptionIsValid() {
        assertNotNull(succesfulRunner.getDescription());
    }

    @Test
    public void testStartRequestsRootPath() {
        taskRunner.runTask(succesfulRunner);
        verify(project, times(1)).getRootPath();
    }

    @Test
    public void testStartFailsOnMavenError() {
        BackgroundTaskListener listener = mock(BackgroundTaskListener.class);

        taskRunner.runTask(failingRunner, listener);
        verify(listener, times(1)).onBegin();
        verify(listener, times(0)).onSuccess();
        verify(listener, times(1)).onFailure();
    }

}