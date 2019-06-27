package fi.helsinki.cs.tmc.core.old.old.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.helsinki.cs.tmc.core.old.old.domain.Course;
import fi.helsinki.cs.tmc.core.old.old.domain.Exercise;
import fi.helsinki.cs.tmc.core.old.old.domain.Project;
import fi.helsinki.cs.tmc.core.old.old.io.FileIO;
import fi.helsinki.cs.tmc.core.old.old.io.IOFactoryImpl;
import fi.helsinki.cs.tmc.core.old.old.io.ProjectScanner;
import fi.helsinki.cs.tmc.core.old.old.storage.CourseStorage;
import fi.helsinki.cs.tmc.core.old.old.storage.DataSource;
import fi.helsinki.cs.tmc.core.old.old.storage.ProjectStorage;

/**
 * Class that initializes the various DAOs.
 */
public class DAOManager {

    public static final String DEFAULT_COURSES_PATH = "courses.tmp";
    public static final String DEFAULT_PROJECTS_PATH = "projects.tmp";

    private final FileIO coursesPath;
    private final FileIO projectsPath;

    private CourseDAO courseDAO;
    private ProjectDAO projectDAO;
    private ReviewDAO reviewDAO;

    public DAOManager() {

        this(new FileIO(DEFAULT_COURSES_PATH), new FileIO(DEFAULT_PROJECTS_PATH));
    }

    public DAOManager(final FileIO coursesPath, final FileIO projectsPath) {

        this.coursesPath = coursesPath;
        this.projectsPath = projectsPath;
    }

    public CourseDAO getCourseDAO() {

        if (courseDAO == null) {
            initialize();
        }

        return courseDAO;
    }

    public ProjectDAO getProjectDAO() {

        if (projectDAO == null) {
            initialize();
        }

        return projectDAO;
    }

    public ReviewDAO getReviewDAO() {

        if (reviewDAO == null) {
            initialize();
        }

        return reviewDAO;
    }

    private void initialize() {

        final DataSource<Course> courseStorage = new CourseStorage(coursesPath);
        courseDAO = new CourseDAO(courseStorage);

        final DataSource<Project> projectStorage = new ProjectStorage(projectsPath);
        projectDAO = new ProjectDAO(projectStorage);

        reviewDAO = new ReviewDAO();

        linkCoursesAndExercises();
        scanProjectFiles();
    }

    private void scanProjectFiles() {

        final ProjectScanner projectScanner = new ProjectScanner(projectDAO, new IOFactoryImpl());
        projectScanner.updateProjects();
        projectDAO.save();
    }

    private void linkCoursesAndExercises() {

        final Map<Course, List<Exercise>> exercisesMap = new HashMap<Course, List<Exercise>>();

        for (final Project project : projectDAO.getProjects()) {
            final Exercise exercise = project.getExercise();
            final Course course = courseDAO.getCourseByName(exercise.getCourseName());
            exercise.setCourse(course);
            exercise.setProject(project);

            if (!exercisesMap.containsKey(course)) {
                exercisesMap.put(course, new ArrayList<Exercise>());
            }
            exercisesMap.get(course).add(exercise);
        }

        for (final Course course : courseDAO.getCourses()) {
            if (exercisesMap.containsKey(course)) {
                course.setExercises(exercisesMap.get(course));
            }
        }
    }

}
