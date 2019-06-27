package fi.helsinki.cs.tmc.core.old.services;

import java.util.List;

import fi.helsinki.cs.tmc.core.old.domain.Course;
import fi.helsinki.cs.tmc.core.old.storage.DataSource;

/**
 * Class that handles Course object loading, saving and accessing.
 */
public class CourseDAO {

    private final DataSource<Course> dataSource;
    private List<Course> courses;

    public CourseDAO(final DataSource<Course> dataSource) {

        this.dataSource = dataSource;
        loadCourses();
    }

    public void loadCourses() {

        courses = dataSource.load();
    }

    public List<Course> getCourses() {

        return courses;
    }

    public void setCourses(final List<Course> courses) {

        this.courses = courses;
        dataSource.save(courses);
    }

    public void updateCourse(final Course course) {

        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getName().equals(course.getName())) {
                courses.set(i, course);
                break;
            }
        }
        dataSource.save(courses);
    }

    public Course getCurrentCourse(final Settings settings) {

        return getCourseByName(settings.getCurrentCourseName());
    }

    public Course getCourseByName(final String name) {

        for (final Course course : courses) {
            if (course.getName().equals(name)) {
                return course;
            }
        }
        return null;
    }

}
