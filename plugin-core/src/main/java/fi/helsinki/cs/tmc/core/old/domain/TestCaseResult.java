package fi.helsinki.cs.tmc.core.old.domain;

import fi.helsinki.cs.tmc.testrunner.CaughtException;
import fi.helsinki.cs.tmc.testrunner.TestCase;

import static fi.helsinki.cs.tmc.testrunner.TestCase.Status.PASSED;

/**
 * Class that stores the result of a single test case.
 */
public class TestCaseResult {

    private String name;
    private boolean successful;
    private String message;
    private CaughtException exception;

    public TestCaseResult() {

    }

    public TestCaseResult(final String name, final boolean successful, final String message) {

        this.name = name;
        this.successful = successful;
        this.message = message;
    }

    public String getName() {

        return name;
    }

    public boolean isSuccessful() {

        return successful;
    }

    // may be null
    public String getMessage() {

        return message;
    }

    // may be null
    public CaughtException getException() {

        return exception;
    }

    /**
     * Creates a TestCaseResult from a TestCase probably returned by a local run
     * of tmc-junit-runner.
     */
    public static TestCaseResult fromTestCaseRecord(final TestCase tc) {

        final TestCaseResult tcr = new TestCaseResult();
        tcr.name = tc.className + " " + tc.methodName;
        tcr.successful = tc.status == PASSED;
        tcr.message = tc.message;
        tcr.exception = tc.exception;
        return tcr;
    }
}
