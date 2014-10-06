package fi.helsinki.cs.tmc.core.utils.jsonhelpers;

import fi.helsinki.cs.tmc.core.domain.Review;

import org.junit.Before;
import org.junit.Test;

public class ReviewListTest {

    private ReviewList rl;

    @Before
    public void setUp() throws Exception {

        rl = new ReviewList();
    }

    @Test
    public void hasNeededFields() {

        // These won't compile if the required fields are missing
        rl.setApiVersion(1);
        rl.setReviews(new Review[1]);
    }

}
