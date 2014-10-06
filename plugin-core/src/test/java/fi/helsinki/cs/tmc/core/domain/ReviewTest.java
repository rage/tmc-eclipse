package fi.helsinki.cs.tmc.core.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReviewTest {

    private Review r;

    @Before
    public void setUp() throws Exception {

        r = new Review();
    }

    @Test
    public void testGettersAndSetters() {

        r.setId(12);
        assertEquals(12, r.getId());

        final Date d = new Date();
        r.setCreatedAt(d);
        assertEquals(d, r.getCreatedAt());

        r.setExerciseName("Exercise");
        assertEquals("Exercise", r.getExerciseName());

        r.setMarkedAsRead(true);
        assertEquals(true, r.isMarkedAsRead());

        final List<String> points = new ArrayList<String>();
        r.setPoints(points);
        assertEquals(points, r.getPoints());

        final List<String> pointsNotAwarded = new ArrayList<String>();
        r.setPointsNotAwarded(pointsNotAwarded);
        assertEquals(pointsNotAwarded, r.getPointsNotAwarded());

        r.setReviewBody("BODY");
        assertEquals("BODY", r.getReviewBody());

        r.setReviewerName("NAME");
        assertEquals("NAME", r.getReviewerName());

        r.setSubmissionId(15);
        assertEquals(15, r.getSubmissionId());

        final Date d2 = new Date();
        r.setUpdatedAt(d2);
        assertEquals(d2, r.getUpdatedAt());

        r.setUpdateUrl("URL");
        assertEquals("URL", r.getUpdateUrl());

        r.setUrl("URL");
        assertEquals("URL", r.getUrl());
    }

    @Test
    public void hashCodeIsSameForTwoReviewsWithSameId() {

        final Review r1 = new Review();
        r1.setId(12);

        final Review r2 = new Review();
        r2.setId(12);

        assertTrue(r1.hashCode() == r2.hashCode());
    }

    @Test
    public void hashCodeNotSameForTwoReviewsWithDifferentIds() {

        final Review r1 = new Review();
        r1.setId(11);

        final Review r2 = new Review();
        r2.setId(12);

        assertTrue(r1.hashCode() != r2.hashCode());
    }

    @Test
    public void reviewIsEqualToItself() {

        final Review r1 = new Review();
        r1.setId(12);

        assertTrue(r1.equals(r1));
    }

    @Test
    public void reviewIsEqualToAReviewWithSameId() {

        final Review r1 = new Review();
        r1.setId(12);

        final Review r2 = new Review();
        r2.setId(12);

        assertTrue(r1.equals(r2));
    }

    @Test
    public void reviewIsNotEqualToAReviewWithDifferentId() {

        final Review r1 = new Review();
        r1.setId(12);

        final Review r2 = new Review();
        r2.setId(11);

        assertTrue(!r1.equals(r2));
    }

    @Test
    public void reviewIsNotEqualToNull() {

        final Review r1 = new Review();
        r1.setId(12);

        final Review r2 = null;

        assertTrue(!r1.equals(r2));
    }

    @Test
    public void reviewIsNotEqualToObjectOfAnotherClass() {

        final Review r1 = new Review();
        r1.setId(12);

        final String r2 = "";

        assertTrue(!r1.equals(r2));
    }
}
