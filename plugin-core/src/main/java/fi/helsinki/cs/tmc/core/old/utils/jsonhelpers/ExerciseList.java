package fi.helsinki.cs.tmc.core.old.old.utils.jsonhelpers;

import com.google.gson.annotations.SerializedName;

import fi.helsinki.cs.tmc.core.old.old.domain.Course;
import fi.helsinki.cs.tmc.core.old.old.domain.Exercise;

import java.util.List;

public class ExerciseList {

    @SerializedName("api_version")
    private String apiVersion;

    @SerializedName("course")
    private Course course;

    public List<Exercise> getExercises() {

        for (final Exercise e : course.getExercises()) {
            e.setCourseName(course.getName());
        }

        return course.getExercises();
    }

    public void setCourse(final Course course) {

        this.course = course;
    }

}
