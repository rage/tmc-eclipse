package fi.helsinki.cs.tmc.core.old.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A domain class for storing necessary data for a single TMC exercise, such as
 * its deadline, name and various URLs.
 */
public class Exercise implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";

    private int id;

    private String name;

    private transient Course course;

    private String courseName;

    private Date deadlineDate;

    @SerializedName("deadline")
    private String deadlineString;

    /**
     * The URL this exercise can be downloaded from.
     */
    @SerializedName("zip_url")
    private String downloadUrl;

    /**
     * The URL the solution can be downloaded from (admins only).
     */
    @SerializedName("solution_zip_url")
    private String solutionDownloadUrl;

    /**
     * The URL where this exercise should be posted for review.
     */
    @SerializedName("return_url")
    private String returnUrl;

    private boolean locked;

    @SerializedName("deadline_description")
    private String deadlineDescription;

    private boolean returnable;
    @SerializedName("requires_review")
    private boolean requiresReview;
    private boolean attempted;
    private boolean completed;
    private boolean reviewed;
    @SerializedName("all_review_points_given")
    private boolean allReviewPointsGiven;

    private String oldChecksum;
    private boolean updateAvailable;

    private String checksum;

    private transient Project project;

    @SerializedName("memory_limit")
    private Integer memoryLimit;

    public Exercise() {

    }

    public Exercise(final String name) {

        this(name, "unknown-course");
    }

    public Exercise(final String name, final String courseName) {

        this.name = name;
        this.courseName = courseName;
    }

    /**
     * This method exists because the API used by the original TMC-plugin was
     * deprecated. Originally Date's constructor accepted strings that contained
     * date and it parsed them. This functionality has now been split to
     * SimpleDateFormat class. As GSON uses reflection when deserializing
     * objects, we can store the date string but we can't create Date object
     * from it. Therefore we deserialize the date to separate string and then
     * call this method to create Date-object from the string
     */
    public void finalizeDeserialization() {

        if (deadlineString == null || deadlineString.equals("")) {
            return;
        }

        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            deadlineDate = sdf.parse(deadlineString);
        } catch (final ParseException e) {
            // Set to null on failure
            // TODO: Log here?
            deadlineDate = null;
        }
    }

    public void setDeadlineString(final String deadline) {

        deadlineString = deadline;
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

        if (name == null) {
            throw new NullPointerException("name was null at Exercise.setName");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty at Exercise.setName");
        }
        this.name = name;
    }

    public boolean isLocked() {

        return locked;
    }

    public void setLocked(final boolean locked) {

        this.locked = locked;
    }

    public String getDeadlineDescription() {

        return deadlineDescription;
    }

    public void setDeadlineDescription(final String deadlineDescription) {

        this.deadlineDescription = deadlineDescription;
    }

    public boolean hasDeadlinePassed() {

        return hasDeadlinePassedAt(new Date());
    }

    public boolean hasDeadlinePassedAt(final Date time) {

        if (time == null) {
            throw new NullPointerException("Given time was null at Exercise.isDeadlineEnded");
        }
        if (getDeadline() != null) {
            return getDeadline().getTime() < time.getTime();
        } else {
            return false;
        }
    }

    public ExerciseKey getKey() {

        return new ExerciseKey(courseName, name);
    }

    public Course getCourse() {

        return course;
    }

    public void setCourse(final Course course) {

        this.course = course;
    }

    public String getCourseName() {

        return courseName;
    }

    public void setCourseName(final String courseName) {

        this.courseName = courseName;
    }

    public String getDownloadUrl() {

        return downloadUrl;
    }

    public void setDownloadUrl(final String downloadAddress) {

        if (downloadAddress == null) {
            throw new NullPointerException("downloadAddress was null at Exercise.setDownloadAddress");
        }
        if (downloadAddress.isEmpty()) {
            throw new IllegalArgumentException("downloadAddress cannot be empty at Exercise.setDownloadAddress");
        }

        downloadUrl = downloadAddress;
    }

    public String getReturnUrl() {

        return returnUrl;
    }

    public void setSolutionDownloadUrl(final String solutionDownloadUrl) {

        this.solutionDownloadUrl = solutionDownloadUrl;
    }

    public String getSolutionDownloadUrl() {

        return solutionDownloadUrl;
    }

    public void setReturnUrl(final String returnAddress) {

        if (returnAddress == null) {
            throw new NullPointerException("returnAddress was null at Exercise.setReturnAddress");
        }
        if (returnAddress.isEmpty()) {
            throw new IllegalArgumentException("downloadAddress cannot be empty at Exercise.setReturnAddress");
        }
        returnUrl = returnAddress;
    }

    public Date getDeadline() {

        return deadlineDate;
    }

    public void setDeadline(final Date deadline) {

        deadlineDate = deadline;
    }

    public boolean isReturnable() {

        return returnable && !hasDeadlinePassed();
    }

    public void setReturnable(final boolean returnable) {

        this.returnable = returnable;
    }

    public boolean requiresReview() {

        return requiresReview;
    }

    public void setRequiresReview(final boolean requiresReview) {

        this.requiresReview = requiresReview;
    }

    public boolean isAttempted() {

        return attempted;
    }

    public void setAttempted(final boolean attempted) {

        this.attempted = attempted;
    }

    public boolean isCompleted() {

        return completed;
    }

    public void setCompleted(final boolean completed) {

        this.completed = completed;
    }

    public boolean isReviewed() {

        return reviewed;
    }

    public void setReviewed(final boolean reviewed) {

        this.reviewed = reviewed;
    }

    public boolean isAllReviewPointsGiven() {

        return allReviewPointsGiven;
    }

    public void setAllReviewPointsGiven(final boolean allReviewPointsGiven) {

        this.allReviewPointsGiven = allReviewPointsGiven;
    }

    public String getChecksum() {

        return checksum;
    }

    public void setChecksum(final String checksum) {

        this.checksum = checksum;
    }

    public void setOldChecksum(final String oldChecksum) {

        this.oldChecksum = oldChecksum;
    }

    public String getOldChecksum() {

        return oldChecksum;
    }

    public Integer getMemoryLimit() {

        return memoryLimit;
    }

    public void setMemoryLimit(final Integer memoryLimit) {

        this.memoryLimit = memoryLimit;
    }

    @Override
    public String toString() {

        return name;
    }

    @Override
    public boolean equals(final Object o) {

        if (o == null || !(o instanceof Exercise)) {
            return false;
        }

        final Exercise e = (Exercise) o;
        if (courseName == null || e.courseName == null) {
            return false;
        }
        if (name == null || e.name == null) {
            return false;
        }

        return courseName.equals(e.courseName) && name.equals(e.name);
    }

    @Override
    public int hashCode() {

        return courseName.hashCode() + 7 * name.hashCode();
    }

    public Project getProject() {

        return project;
    }

    public void setProject(final Project project) {

        this.project = project;
    }

    public void setUpdateAvailable(final boolean updateAvailable) {

        this.updateAvailable = updateAvailable;
    }

    public boolean isUpdateAvailable() {

        return updateAvailable;
    }

    public boolean shouldBeUpdated() {

        if (!updateAvailable) {
            if (oldChecksum == null || oldChecksum.equals(checksum)) {
                return false;
            } else {
                setUpdateAvailable(true);
                return true;
            }
        }

        return true;
    }

    public boolean isDownloadable() {

        return (!hasDeadlinePassed()) && (project == null || project.getStatus() != ProjectStatus.DOWNLOADED);
    }

}
