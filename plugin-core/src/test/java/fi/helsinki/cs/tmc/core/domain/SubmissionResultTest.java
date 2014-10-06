package fi.helsinki.cs.tmc.core.domain;

import fi.helsinki.cs.tmc.core.domain.SubmissionResult.Status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SubmissionResultTest {

    private SubmissionResult result;

    @Before
    public void setUp() throws Exception {

        result = new SubmissionResult();
        result.setError("error XD");
        result.setFeedbackAnswerUrl("answer url");

        final List<FeedbackQuestion> fbgList = new ArrayList<FeedbackQuestion>();
        fbgList.add(new FeedbackQuestion(0, "question1", "kind1"));
        fbgList.add(new FeedbackQuestion(0, "question2", "kind2"));
        fbgList.add(new FeedbackQuestion(0, "question3", "kind3"));

        result.setFeedbackQuestions(fbgList);

        final List<String> mrpList = new ArrayList<String>();
        mrpList.add("a");
        mrpList.add("bb");
        mrpList.add("ccc");
        result.setMissingReviewPoints(mrpList);

        final List<String> pointList = new ArrayList<String>();
        pointList.add("point1");
        pointList.add("point2");
        pointList.add("point3");
        result.setPoints(pointList);

        result.setSolutionUrl("solution url");
        result.setStatus(Status.OK);

        final List<TestCaseResult> tcrList = new ArrayList<TestCaseResult>();
        tcrList.add(new TestCaseResult("name1", true, "message1"));
        tcrList.add(new TestCaseResult("name2", false, "message2"));
        tcrList.add(new TestCaseResult("name3", true, "message3"));
        result.setTestCases(tcrList);

    }

    @Test
    public void getters() {

        assertEquals(result.getError(), "error XD");
        assertEquals(result.getFeedbackAnswerUrl(), "answer url");
        assertEquals(result.getFeedbackQuestions().size(), 3);
        assertEquals(result.getMissingReviewPoints().size(), 3);
        assertEquals(result.getPoints().size(), 3);
        assertEquals(result.getSolutionUrl(), "solution url");
        assertEquals(result.getTestCases().size(), 3);
        assertEquals(result.getStatus(), Status.OK);
    }

    @Test
    public void allTestFailedTest() {

        assertFalse(result.allTestCasesFailed());

        final List<TestCaseResult> l = new ArrayList<TestCaseResult>();
        result.setTestCases(l);
        assertTrue(result.allTestCasesFailed());

        l.add(new TestCaseResult("", false, ""));
        l.add(new TestCaseResult("", false, ""));
        assertTrue(result.allTestCasesFailed());
    }

    @Test
    public void allTestPassedTest() {

        assertFalse(result.allTestCasesSucceeded());

        final List<TestCaseResult> l = new ArrayList<TestCaseResult>();
        result.setTestCases(l);
        assertTrue(result.allTestCasesSucceeded());

        l.add(new TestCaseResult("", true, ""));
        l.add(new TestCaseResult("", true, ""));
        assertTrue(result.allTestCasesSucceeded());
    }

}
