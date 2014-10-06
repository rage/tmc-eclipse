package fi.helsinki.cs.tmc.core.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FeedbackAnswerTest {

    private FeedbackAnswer answer;

    @Before
    public void setUp() {

        answer = new FeedbackAnswer(new FeedbackQuestion(0, "question1", "kind1"));
    }

    @Test
    public void toJsonTest() {

        answer.setAnswer("answer1");
        answer.setQuestion(new FeedbackQuestion(1, "question2", "kind2"));
        assertEquals(answer.toJson(), "{\"question_id\":1,\"answer\":\"answer1\"}");
    }

}
