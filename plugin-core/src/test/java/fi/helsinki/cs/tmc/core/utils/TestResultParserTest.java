package fi.helsinki.cs.tmc.core.utils;

import fi.helsinki.cs.tmc.core.domain.TestCaseResult;
import fi.helsinki.cs.tmc.testrunner.TestCase;
import fi.helsinki.cs.tmc.testrunner.TestCaseList;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestResultParserTest {

    private final String json = "[{\"className\":\"testClass\",\"methodName\":\"testMethod\",\"pointNames\":[\"a\",\"b\",\"c\"],\"status\":\"NOT_STARTED\"},{\"className\":\"anotherTestClass\",\"methodName\":\"anotherTestMethod\",\"pointNames\":[\"d\",\"e\",\"f\"],\"status\":\"NOT_STARTED\"}]";

    private TestResultParser parser;
    private TestCaseList list;

    @Before
    public void setUp() {

        parser = new TestResultParser();

        list = new TestCaseList();

        final String[] points = { "a", "b", "c" };
        final TestCase a = new TestCase("testClass", "testMethod", points);

        final String[] points2 = { "d", "e", "f" };
        final TestCase b = new TestCase("anotherTestClass", "anotherTestMethod", points2);

        list.add(a);
        list.add(b);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseTestResultsWithInvalidJson() {

        final List<TestCaseResult> results = parser.parseTestResults("").getTestCaseResults();
    }

    @Test
    public void testParseTestResultsWithValidJson() throws IOException {

        final List<TestCaseResult> results = parser.parseTestResults(json).getTestCaseResults();

        for (int i = 0; i < results.size(); i++) {
            final TestCaseResult res = results.get(i);
            assertEquals(res.getName(), list.get(i).className + " " + list.get(i).methodName);
            assertNull(res.getMessage());
            assertFalse(res.isSuccessful());
        }
    }
}
