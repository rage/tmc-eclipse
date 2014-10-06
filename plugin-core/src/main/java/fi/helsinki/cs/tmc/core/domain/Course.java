package fi.helsinki.cs.tmc.core.domain;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * A domain class for storing course information. This class is also used when
 * deserializing data from the server.
 */
public class Course {

    private int id;
    private String name;

    @SerializedName("details_url")
    private String detailsUrl;

    @SerializedName("unlock_url")
    private String unlockUrl;

    @SerializedName("reviews_url")
    private String reviewsUrl;

    @SerializedName("comet_url")
    private String cometUrl;

    @SerializedName("spyware_urls")
    private List<String> spywareUrls;

    private boolean exercisesLoaded;

    private List<Exercise> exercises;
    private List<String> unlockables;

    public Course() {

        /*
         * In case of a missing name-field in the JSON, GSON would replace any
         * default value of name with null. Therefore using f.ex. empty string
         * here is useless.
         */
        this(null);
    }

    public Course(final String name) {

        this.name = name;
        exercises = new ArrayList<Exercise>();
        unlockables = new ArrayList<String>();
        spywareUrls = new ArrayList<String>();
    }

    public int getId() {

        return id;
    }

    public void setId(final int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(final String name) {

        this.name = name;
    }

    public String getDetailsUrl() {

        return detailsUrl;
    }

    public void setDetailsUrl(final String detailsUrl) {

        this.detailsUrl = detailsUrl;
    }

    public String getUnlockUrl() {

        return unlockUrl;
    }

    public void setUnlockUrl(final String unlockUrl) {

        this.unlockUrl = unlockUrl;
    }

    public String getReviewsUrl() {

        return reviewsUrl;
    }

    public void setReviewsUrl(final String reviewsUrl) {

        this.reviewsUrl = reviewsUrl;
    }

    public String getCometUrl() {

        return cometUrl;
    }

    public void setCometUrl(final String cometUrl) {

        this.cometUrl = cometUrl;
    }

    public List<String> getSpywareUrls() {

        return spywareUrls;
    }

    public void setSpywareUrls(final List<String> spywareUrls) {

        this.spywareUrls = spywareUrls;
    }

    public boolean isExercisesLoaded() {

        return exercisesLoaded;
    }

    public void setExercisesLoaded(final boolean exercisesLoaded) {

        this.exercisesLoaded = exercisesLoaded;
    }

    public List<String> getUnlockables() {

        return unlockables;
    }

    public void setUnlockables(final List<String> unlockables) {

        this.unlockables = unlockables;
    }

    public List<Exercise> getDownloadableExercises() {

        final List<Exercise> downloadableExercises = new ArrayList<Exercise>();
        for (final Exercise e : getExercises()) {
            if ((e.isDownloadable() || e.shouldBeUpdated()) && !e.isCompleted()) {
                downloadableExercises.add(e);
            }
        }
        return downloadableExercises;
    }

    public List<Exercise> getCompletedDownloadableExercises() {

        final List<Exercise> completedExercises = new ArrayList<Exercise>();
        for (final Exercise e : getExercises()) {
            if ((e.isDownloadable() || e.shouldBeUpdated()) && e.isCompleted()) {
                completedExercises.add(e);
            }
        }
        return completedExercises;
    }

    public List<Exercise> getExercises() {

        return exercises;
    }

    public void setExercises(final List<Exercise> exercises) {

        this.exercises = exercises;
    }

    @Override
    public String toString() {

        return name;
    }

    @Override
    public int hashCode() {

        return 31 * 1 + ((name == null) ? 0 : name.hashCode());
    }

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }
            
        if (other == null) {
            return false;
        }
            
        if (getClass() != other.getClass()) {
            return false;
        }
            
        final Course otherCourse = (Course) other;
        
        if (name == null) {
            if (otherCourse.name != null) {
                return false;
            }
        } else if (!name.equals(otherCourse.name)) {
            return false;
        }
            
        return true;
    }
}
