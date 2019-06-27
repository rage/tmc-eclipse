package fi.helsinki.cs.tmc.core.old.old.utils.jsonhelpers;

import fi.helsinki.cs.tmc.core.old.old.domain.Review;

public class ReviewList {

    private int apiVersion;
    private Review[] reviews;

    public int getApiVersion() {

        return apiVersion;
    }

    public void setApiVersion(final int apiVersion) {

        this.apiVersion = apiVersion;
    }

    public Review[] getReviews() {

        return reviews;
    }

    public void setReviews(final Review[] reviews) {

        this.reviews = reviews;
    }
}
