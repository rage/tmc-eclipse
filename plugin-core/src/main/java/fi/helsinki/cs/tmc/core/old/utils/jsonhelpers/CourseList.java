package fi.helsinki.cs.tmc.core.old.utils.jsonhelpers;

import com.google.gson.annotations.SerializedName;

import fi.helsinki.cs.tmc.core.old.domain.Course;

public class CourseList {

    @SerializedName("api_version")
    private String apiVersion;
    @SerializedName("courses")
    private Course[] courses;

    public String getApiVersion() {

        return apiVersion;
    }

    public void setApiVersion(final String apiVersion) {

        this.apiVersion = apiVersion;
    }

    public Course[] getCourses() {

        return courses;
    }

    public void setCourses(final Course[] courses) {

        this.courses = courses;
    }
}
