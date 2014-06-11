package fi.helsinki.cs.plugin.tmc.ui;

import java.util.List;

import fi.helsinki.cs.plugin.tmc.domain.SubmissionResult;
import fi.helsinki.cs.plugin.tmc.domain.TestCaseResult;
import fi.helsinki.cs.plugin.tmc.services.http.SubmissionResponse;

public interface IdeUIInvoker {
    public void invokeTestResultWindow(List<TestCaseResult> results);

    public void invokeAllTestsPassedWindow(SubmissionResult result, String exerciseName);

    public void invokeSomeTestsFailedWindow(SubmissionResult result, String exerciseName);

    public void invokeAllTestsFailedWindow(SubmissionResult result, String exerciseName);

    public void invokeSubmitToServerWindow();

    public void invokeSendToPastebinWindow(String exerciseName);

    public void invokePastebinResultDialog(String pasteUrl);

}
