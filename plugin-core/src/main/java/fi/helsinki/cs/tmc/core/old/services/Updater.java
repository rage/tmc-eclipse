package fi.helsinki.cs.tmc.core.old.old.services;

import java.util.List;

import fi.helsinki.cs.tmc.core.old.old.domain.Course;
import fi.helsinki.cs.tmc.core.old.old.domain.Exercise;
import fi.helsinki.cs.tmc.core.old.old.domain.Project;
import fi.helsinki.cs.tmc.core.old.old.services.http.ServerManager;
import fi.helsinki.cs.tmc.core.old.old.ui.UserVisibleException;

/**
 * Class that handles updating necessary data when we update exercise from
 * server.
 */
public class Updater {

    private final ServerManager server;
    private final CourseDAO courseDAO;
    private final ProjectDAO projectDAO;

    public Updater(final ServerManager server, final CourseDAO courseDAO, final ProjectDAO projectDAO) {

        this.server = server;
        this.courseDAO = courseDAO;
        this.projectDAO = projectDAO;
    }

    public void updateCourses() {

        final List<Course> oldCourses = courseDAO.getCourses();
        final List<Course> newCourses = server.getCourses();

        for (final Course newCourse : newCourses) {
            final Course oldCourse = findEqualCourse(oldCourses, newCourse);
            if (oldCourse != null) {
                updateCourse(oldCourse, newCourse);
            }
        }

        courseDAO.setCourses(newCourses);
    }

    private void updateCourse(final Course oldCourse, final Course newCourse) {

        if (oldCourse.getExercises() != null) {
            for (final Exercise e : oldCourse.getExercises()) {
                // Update Exercise.course
                e.setCourse(newCourse);
            }
            // Update Course.exercise
            newCourse.setExercises(oldCourse.getExercises());
        }
    }

    private Course findEqualCourse(final List<Course> list, final Course course) {

        for (final Course c : list) {
            if (c.equals(course)) {
                return c;
            }
        }
        return null;
    }

    public void updateExercises(final Course course) {

        if (course == null) {
            throw new UserVisibleException("Remember to select your course from TMC -> Settings");
        }

        final List<Exercise> oldExercises = course.getExercises();
        final List<Exercise> newExercises = server.getExercises(course.getId() + "");

        for (final Exercise newExercise : newExercises) {
            final Exercise oldExercise = findEqualExercise(oldExercises, newExercise);
            if (oldExercise != null) {
                updateExercise(oldExercise, newExercise);
            }
        }

        course.setExercises(newExercises);
    }

    private void updateExercise(final Exercise oldExercise, final Exercise newExercise) {

        // Update Exercise.course
        newExercise.setOldChecksum(oldExercise.getChecksum());
        newExercise.setUpdateAvailable(oldExercise.isUpdateAvailable());
        newExercise.setCourse(oldExercise.getCourse());
        newExercise.setProject(oldExercise.getProject());

        // Update Project.exercise
        final Project project = projectDAO.getProjectByExercise(oldExercise);
        if (project != null) {
            project.setExercise(newExercise);
        }
    }

    private Exercise findEqualExercise(final List<Exercise> list, final Exercise exercise) {

        for (final Exercise e : list) {
            if (e.equals(exercise)) {
                return e;
            }
        }
        return null;
    }

}
