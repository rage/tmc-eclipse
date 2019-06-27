package fi.helsinki.cs.tmc.core.old.services;

import java.util.ArrayList;
import java.util.List;

import fi.helsinki.cs.tmc.core.old.domain.Review;

/**
 * Class that handles storing the code reviews locally. Prevents the situation
 * where same review is shown multiple times (after each review background
 * check) if user has not marked them as read. The reviews will only be shown
 * again during new session.
 */
public class ReviewDAO {

    private final List<Review> reviews;

    public ReviewDAO() {

        reviews = new ArrayList<Review>();
    }

    public boolean add(final Review newReview) {

        if (!reviews.contains(newReview)) {
            reviews.add(newReview);
            return true;
        } else {
            return overwriteIfNecessary(newReview);
        }
    }

    private boolean overwriteIfNecessary(final Review newReview) {

        final int index = reviews.indexOf(newReview);
        final Review current = reviews.get(index);

        if (current.getUpdatedAt().before(newReview.getUpdatedAt())) {
            reviews.set(index, newReview);
            return true;
        } else {
            return false;
        }
    }

    public void addAll(final List<Review> reviews) {

        for (final Review r : reviews) {
            add(r);
        }
    }

    public List<Review> all() {

        return reviews;
    }

    public List<Review> unseen() {

        final List<Review> unseen = new ArrayList<Review>();
        for (final Review r : reviews) {
            if (!r.isMarkedAsRead()) {
                unseen.add(r);
            }
        }
        return unseen;
    }
}
