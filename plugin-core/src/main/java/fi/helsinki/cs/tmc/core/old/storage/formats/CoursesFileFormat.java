package fi.helsinki.cs.tmc.core.old.old.storage.formats;

import java.util.List;

import fi.helsinki.cs.tmc.core.old.old.domain.Course;

public class CoursesFileFormat {

    private List<Course> courses;

    public List<Course> getCourses() {

        return courses;
    }

    public void setCourses(final List<Course> courses) {

        this.courses = courses;
    }

}
