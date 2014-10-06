package fi.helsinki.cs.tmc.core.async.listeners;

import fi.helsinki.cs.tmc.core.async.tasks.UploaderTask;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.domain.SubmissionResult;
import fi.helsinki.cs.tmc.core.domain.TestCaseResult;
import fi.helsinki.cs.tmc.core.ui.IdeUIInvoker;
import fi.helsinki.cs.tmc.core.utils.ProjectIconHandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UploadTaskListenerTest {

    private UploadTaskListener listener;
    private UploaderTask task;
    private IdeUIInvoker invoker;
    private ProjectIconHandler handler;

    @Before
    public void setUp() {

        invoker = mock(IdeUIInvoker.class);
        task = mock(UploaderTask.class);
        handler = mock(ProjectIconHandler.class);
        listener = new UploadTaskListener(task, invoker, handler);
    }

    @Test
    public void noExtraMethodsCalledIfSubmissionResultIsNull() {

        when(task.getResult()).thenReturn(null);

        listener.onSuccess();

        verify(task, times(1)).getResult();
        verify(task, times(0)).getProject();

        verify(invoker, times(0)).invokeTestResultWindow(Matchers.anyListOf(TestCaseResult.class));
        verify(invoker, times(0)).invokeAllTestsPassedWindow(Matchers.any(SubmissionResult.class), Matchers.anyString());
        verify(invoker, times(0)).invokeAllTestsFailedWindow(Matchers.any(SubmissionResult.class), Matchers.anyString());
        verify(invoker, times(0)).invokeSomeTestsFailedWindow(Matchers.any(SubmissionResult.class), Matchers.anyString());
    }

    @Test
    public void correctMethodsCalledIfAllTestCasesSucceeded() {

        final SubmissionResult result = mock(SubmissionResult.class);
        when(result.allTestCasesSucceeded()).thenReturn(true);
        when(result.allTestCasesFailed()).thenReturn(false);

        final Exercise exercise = mock(Exercise.class);
        when(exercise.getName()).thenReturn("foo");

        final Project project = mock(Project.class);
        when(project.getExercise()).thenReturn(exercise);

        when(task.getProject()).thenReturn(project);

        when(task.getResult()).thenReturn(result);
        listener.onSuccess();

        verify(task, times(1)).getResult();
        verify(task, times(2)).getProject();

        verify(invoker, times(1)).invokeTestResultWindow(Matchers.anyListOf(TestCaseResult.class));
        verify(invoker, times(1)).invokeAllTestsPassedWindow(Matchers.any(SubmissionResult.class), Matchers.anyString());
        verify(invoker, times(0)).invokeAllTestsFailedWindow(Matchers.any(SubmissionResult.class), Matchers.anyString());
        verify(invoker, times(0)).invokeSomeTestsFailedWindow(Matchers.any(SubmissionResult.class), Matchers.anyString());
    }

    @Test
    public void correctMethodsCalledIfAllTestCasesFail() {

        final SubmissionResult result = mock(SubmissionResult.class);
        when(result.allTestCasesSucceeded()).thenReturn(false);
        when(result.allTestCasesFailed()).thenReturn(true);

        final Exercise exercise = mock(Exercise.class);
        when(exercise.getName()).thenReturn("foo");

        final Project project = mock(Project.class);
        when(project.getExercise()).thenReturn(exercise);

        when(task.getProject()).thenReturn(project);

        when(task.getResult()).thenReturn(result);
        listener.onSuccess();

        verify(task, times(1)).getResult();
        verify(task, times(2)).getProject();

        verify(invoker, times(1)).invokeTestResultWindow(Matchers.anyListOf(TestCaseResult.class));
        verify(invoker, times(0)).invokeAllTestsPassedWindow(Matchers.any(SubmissionResult.class), Matchers.anyString());
        verify(invoker, times(1)).invokeAllTestsFailedWindow(Matchers.any(SubmissionResult.class), Matchers.anyString());
        verify(invoker, times(0)).invokeSomeTestsFailedWindow(Matchers.any(SubmissionResult.class), Matchers.anyString());
    }

    @Test
    public void correctMethodsCalledIfSomeTestCasesFail() {

        final SubmissionResult result = mock(SubmissionResult.class);
        when(result.allTestCasesSucceeded()).thenReturn(false);
        when(result.allTestCasesFailed()).thenReturn(false);

        final Exercise exercise = mock(Exercise.class);
        when(exercise.getName()).thenReturn("foo");

        final Project project = mock(Project.class);
        when(project.getExercise()).thenReturn(exercise);

        when(task.getProject()).thenReturn(project);

        when(task.getResult()).thenReturn(result);
        listener.onSuccess();

        verify(task, times(1)).getResult();
        verify(task, times(2)).getProject();

        verify(invoker, times(1)).invokeTestResultWindow(Matchers.anyListOf(TestCaseResult.class));
        verify(invoker, times(0)).invokeAllTestsPassedWindow(Matchers.any(SubmissionResult.class), Matchers.anyString());
        verify(invoker, times(0)).invokeAllTestsFailedWindow(Matchers.any(SubmissionResult.class), Matchers.anyString());
        verify(invoker, times(1)).invokeSomeTestsFailedWindow(Matchers.any(SubmissionResult.class), Matchers.anyString());
    }
}
