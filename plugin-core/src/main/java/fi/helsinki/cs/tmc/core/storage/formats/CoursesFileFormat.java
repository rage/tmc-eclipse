package fi.helsinki.cs.tmc.core.storage.formats;

import fi.helsinki.cs.tmc.core.domain.Course;

import java.util.List;

public class CoursesFileFormat {

    private List<Course> courses;

    public List<Course> getCourses() {

        return courses;
    }

    public void setCourses(final List<Course> courses) {

        this.courses = courses;
    }

}
