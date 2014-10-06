package fi.helsinki.cs.tmc.core.async.listeners;

import fi.helsinki.cs.tmc.core.async.tasks.CodeReviewRequestTask;
import fi.helsinki.cs.tmc.core.ui.IdeUIInvoker;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class CodeReviewRequestListenerTest {

    private CodeReviewRequestTask task;
    private IdeUIInvoker invoker;
    private CodeReviewRequestListener listener;

    @Before
    public void setUp() {

        task = mock(CodeReviewRequestTask.class);
        invoker = mock(IdeUIInvoker.class);
        listener = new CodeReviewRequestListener(task, invoker);
    }

    @Test
    public void invokesTheUIAfterSuccess() {

        listener.onSuccess();
        verify(invoker, times(1)).invokeCodeReviewRequestSuccefullySentWindow();
    }

    @Test
    public void raisesAnErrorOnFailure() {

        listener.onFailure();
        verify(invoker, times(1)).raiseVisibleException("Failed to create the code review request.");
    }

    @Test
    public void doNothingOnBegin() {

        listener.onBegin();
        verifyNoMoreInteractions(task, invoker);
    }

}
