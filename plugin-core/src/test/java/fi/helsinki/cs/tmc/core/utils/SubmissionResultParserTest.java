package fi.helsinki.cs.tmc.core.utils;

import com.google.gson.Gson;

import fi.helsinki.cs.tmc.core.domain.SubmissionResult;
import fi.helsinki.cs.tmc.core.domain.SubmissionResult.Status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubmissionResultParserTest {

    private SubmissionResultParser parser;

    @Before
    public void setUp() {

        parser = new SubmissionResultParser();
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseFromJsonThrowsWithEmptyArgument() {

        parser.parseFromJson("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseFromJsonThrowsWithWhitespaceArgument() {

        parser.parseFromJson("          ");
    }

    @Test(expected = RuntimeException.class)
    public void parseFromJsonThrowsRuntimeExceptionOnMalformedJson() {

        parser.parseFromJson("This is invalid json");
    }

    @Test
    public void parseFromJsonReturnsValidObjectWhenDeserializing() {

        final SubmissionResult initial = new SubmissionResult();
        initial.setError("Error");
        initial.setStatus(Status.OK);
        final List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        initial.setMissingReviewPoints(list);

        final Gson gson = new Gson();
        final SubmissionResult result = parser.parseFromJson(gson.toJson(initial));
        assertEquals(initial.getError(), result.getError());
        assertEquals(initial.getStatus(), result.getStatus());
        assertEquals(initial.getMissingReviewPoints(), result.getMissingReviewPoints());
    }
}
