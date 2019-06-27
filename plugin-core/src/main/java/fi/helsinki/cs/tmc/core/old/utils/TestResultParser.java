package fi.helsinki.cs.tmc.core.old.old.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.helsinki.cs.tmc.core.old.old.domain.TestCaseResult;
import fi.helsinki.cs.tmc.core.old.old.domain.TestRunResult;
import fi.helsinki.cs.tmc.testrunner.StackTraceSerializer;
import fi.helsinki.cs.tmc.testrunner.TestCase;
import fi.helsinki.cs.tmc.testrunner.TestCaseList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class TestResultParser {

    public TestRunResult parseTestResults(final File resultsFile) throws IOException {

        final String resultsJson = FileUtils.readFileToString(resultsFile, "UTF-8");
        return parseTestResults(resultsJson);
    }

    public TestRunResult parseTestResults(final String resultsJson) {

        final Gson gson = new GsonBuilder().registerTypeAdapter(StackTraceElement.class, new StackTraceSerializer()).create();

        final TestCaseList testCaseRecords = gson.fromJson(resultsJson, TestCaseList.class);
        if (testCaseRecords == null) {
            throw new IllegalArgumentException("Empty result from test runner");
        }

        final List<TestCaseResult> testCaseResults = new ArrayList<TestCaseResult>();
        for (final TestCase tc : testCaseRecords) {
            testCaseResults.add(TestCaseResult.fromTestCaseRecord(tc));
        }
        return new TestRunResult(testCaseResults);
    }
}
