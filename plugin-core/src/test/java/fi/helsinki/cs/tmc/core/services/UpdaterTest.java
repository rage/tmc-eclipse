package fi.helsinki.cs.tmc.core.services;

import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.services.http.ServerManager;
import fi.helsinki.cs.tmc.core.ui.UserVisibleException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdaterTest {
    
    private static final String COURSE_NAME = "course_name";
    private static final String EXERCISE_NAME = "exercise_name";

    private ServerManager server;
    private CourseDAO courseDAO;
    private ProjectDAO projectDAO;

    private Updater updater;

    @Before
    public void setUp() {

        server = mock(ServerManager.class);
        courseDAO = mock(CourseDAO.class);
        projectDAO = mock(ProjectDAO.class);

        updater = new Updater(server, courseDAO, projectDAO);
    }

    @Test
    public void testUpdateCoursesCallsDAOandServer() {

        updater.updateCourses();

        verify(server, times(1)).getCourses();
        verify(courseDAO, times(1)).getCourses();

        verify(courseDAO, times(1)).setCourses(any(List.class));
    }

    @Test
    public void testUpdateCoursesUpdatesDAO() {

        final List<Exercise> serverExerciseList = new ArrayList<Exercise>();
        final Exercise serverExercise = new Exercise("serverExercise", "serverCourse");
        serverExerciseList.add(serverExercise);

        final List<Course> serverCourseList = new ArrayList<Course>();
        final Course serverCourse = new Course("serverCourse");
        serverCourseList.add(serverCourse);

        when(server.getCourses()).thenReturn(serverCourseList);
        when(courseDAO.getCourses()).thenReturn(new ArrayList<Course>());

        updater.updateCourses();

        verify(courseDAO, times(1)).setCourses(any(List.class));
    }

    @Test
    public void testUpdateExercisesCallsCourseAndServer() {

        final Course course = mock(Course.class);
        when(course.getId()).thenReturn(1);
        when(course.getExercises()).thenReturn(new ArrayList<Exercise>());

        updater.updateExercises(course);

        verify(server, times(1)).getExercises("1");
        verify(course, times(1)).getExercises();

    }

    @Test
    public void newCourseExercisesAreSetCorrectlyAsOldExercises() {

        final List<Course> serverCourseList = new ArrayList<Course>();
        final Course serverCourse = new Course(COURSE_NAME);
        serverCourseList.add(serverCourse);
        when(server.getCourses()).thenReturn(serverCourseList);

        final List<Course> daoCourseList = new ArrayList<Course>();
        final Course daoCourse = new Course(COURSE_NAME);

        final List<Exercise> exercises = new ArrayList<Exercise>();
        exercises.add(new Exercise(COURSE_NAME, EXERCISE_NAME));
        exercises.get(0).setCourse(daoCourse);
        daoCourse.setExercises(exercises);
        daoCourseList.add(daoCourse);

        when(courseDAO.getCourses()).thenReturn(daoCourseList);

        updater.updateCourses();
        assertEquals(exercises, serverCourse.getExercises());
    }

    @Test
    public void newCourseExercisesHaveCorrectParentCourse() {

        final List<Course> serverCourseList = new ArrayList<Course>();
        final Course serverCourse = new Course(COURSE_NAME);
        serverCourseList.add(serverCourse);
        when(server.getCourses()).thenReturn(serverCourseList);

        final List<Course> daoCourseList = new ArrayList<Course>();
        final Course daoCourse = new Course(COURSE_NAME);

        final List<Exercise> exercises = new ArrayList<Exercise>();
        exercises.add(new Exercise(COURSE_NAME, EXERCISE_NAME));
        exercises.get(0).setCourse(daoCourse);
        daoCourse.setExercises(exercises);
        daoCourseList.add(daoCourse);

        when(courseDAO.getCourses()).thenReturn(daoCourseList);

        updater.updateCourses();

        assertTrue(exercises.get(0).getCourse() == serverCourse);
    }

    @Test
    public void newCourseExercisesAreNotOverriddenIfOldCourseExercisesAreNull() {

        final List<Course> serverCourseList = new ArrayList<Course>();
        final Course serverCourse = new Course(COURSE_NAME);
        serverCourseList.add(serverCourse);
        when(server.getCourses()).thenReturn(serverCourseList);

        final List<Course> daoCourseList = new ArrayList<Course>();
        final Course daoCourse = new Course(COURSE_NAME);
        daoCourseList.add(daoCourse);
        daoCourse.setExercises(null);
        final List<Exercise> exercises = new ArrayList<Exercise>();

        exercises.add(new Exercise(COURSE_NAME, EXERCISE_NAME));
        exercises.get(0).setCourse(serverCourse);

        serverCourse.setExercises(exercises);

        when(courseDAO.getCourses()).thenReturn(daoCourseList);

        updater.updateCourses();

        assertEquals(exercises, serverCourse.getExercises());
    }

    @Test
    public void newCourseExercisesAreNotOverriddenIfOldCourseIsNotEqual() {

        final List<Course> serverCourseList = new ArrayList<Course>();
        final Course serverCourse = new Course(COURSE_NAME);
        serverCourseList.add(serverCourse);
        when(server.getCourses()).thenReturn(serverCourseList);

        final List<Course> daoCourseList = new ArrayList<Course>();
        final Course daoCourse = new Course("different_course_name");
        daoCourseList.add(daoCourse);

        final List<Exercise> exercises = new ArrayList<Exercise>();

        exercises.add(new Exercise(COURSE_NAME, EXERCISE_NAME));
        exercises.get(0).setCourse(serverCourse);

        serverCourse.setExercises(exercises);

        when(courseDAO.getCourses()).thenReturn(daoCourseList);

        updater.updateCourses();

        assertEquals(exercises, serverCourse.getExercises());
    }

    @Test(expected = UserVisibleException.class)
    public void exceptionIsThrownIfCourseIsNull() {

        updater.updateExercises(null);
    }

    @Test
    public void exerciseIsUpdatedWhenThereIsOldExerciseForCourse() throws NoSuchFieldException, IllegalAccessException {

        final Project project = mock(Project.class);

        final Course course = new Course(COURSE_NAME);
        course.setId(1);

        final List<Exercise> exercises = new ArrayList<Exercise>();
        exercises.add(new Exercise(COURSE_NAME, EXERCISE_NAME));
        exercises.add(new Exercise("baz", "qux"));
        exercises.get(1).setChecksum("1234");
        exercises.get(1).setUpdateAvailable(false);
        exercises.get(1).setCourse(course);
        exercises.get(1).setProject(project);

        course.setExercises(exercises);

        final List<Exercise> serverExercises = new ArrayList<Exercise>();
        serverExercises.add(new Exercise("baz", "qux"));
        when(server.getExercises("1")).thenReturn(serverExercises);

        updater.updateExercises(course);

        final Field field = Exercise.class.getDeclaredField("updateAvailable");
        field.setAccessible(true);
        assertEquals(exercises.get(1).getChecksum(), serverExercises.get(0).getOldChecksum());
        assertEquals(field.get(exercises.get(1)), field.get(serverExercises.get(0)));
        assertEquals(exercises.get(1).getCourse(), serverExercises.get(0).getCourse());
        assertEquals(exercises.get(1).getProject(), serverExercises.get(0).getProject());
    }

    @Test
    public void projectHasCorrectExerciseSet() {

        final Project project = mock(Project.class);

        final Course course = new Course(COURSE_NAME);
        course.setId(1);

        final List<Exercise> exercises = new ArrayList<Exercise>();
        exercises.add(new Exercise(COURSE_NAME, EXERCISE_NAME));
        exercises.add(new Exercise("baz", "qux"));
        exercises.get(1).setChecksum("1234");
        exercises.get(1).setUpdateAvailable(false);
        exercises.get(1).setCourse(course);
        exercises.get(1).setProject(project);

        course.setExercises(exercises);

        final List<Exercise> serverExercises = new ArrayList<Exercise>();
        serverExercises.add(new Exercise("baz", "qux"));

        when(server.getExercises("1")).thenReturn(serverExercises);
        when(projectDAO.getProjectByExercise(exercises.get(1))).thenReturn(project);

        updater.updateExercises(course);
        verify(project, times(1)).setExercise(serverExercises.get(0));
    }

    @Test
    public void serverExerciseIsNotModifiedIfNoLocalMatchIsPresent() throws IllegalAccessException, NoSuchFieldException {

        final Project project = mock(Project.class);

        final Course course = new Course(COURSE_NAME);
        course.setId(1);

        final List<Exercise> exercises = new ArrayList<Exercise>();
        exercises.add(new Exercise(COURSE_NAME, EXERCISE_NAME));

        course.setExercises(exercises);

        final List<Exercise> serverExercises = new ArrayList<Exercise>();
        serverExercises.add(new Exercise("baz", "qux"));

        serverExercises.get(0).setOldChecksum("1234");
        serverExercises.get(0).setUpdateAvailable(false);
        serverExercises.get(0).setCourse(course);
        serverExercises.get(0).setProject(project);

        when(server.getExercises("1")).thenReturn(serverExercises);

        updater.updateExercises(course);

        final Field field = Exercise.class.getDeclaredField("updateAvailable");
        field.setAccessible(true);

        assertEquals("1234", serverExercises.get(0).getOldChecksum());
        assertEquals(false, field.get(serverExercises.get(0)));
        assertEquals(course, serverExercises.get(0).getCourse());
        assertEquals(project, serverExercises.get(0).getProject());
    }

}
