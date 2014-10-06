package fi.helsinki.cs.tmc.core.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.ExerciseKey;
import fi.helsinki.cs.tmc.core.io.FileIO;
import fi.helsinki.cs.tmc.core.storage.formats.CoursesFileFormat;
import fi.helsinki.cs.tmc.core.ui.UserVisibleException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CourseStorage implements DataSource<Course> {

    private final Gson gson;
    private final FileIO io;

    public CourseStorage(final FileIO io) {

        this.io = io;
        gson = createGson();
    }

    @Override
    public List<Course> load() {

        if (!io.fileExists()) {
            return new ArrayList<Course>();
        }
        CoursesFileFormat courseList = null;
        final Reader reader = io.getReader();
        if (reader == null) {
            throw new UserVisibleException("Could not load course data from local storage.");
        }
        try {
            courseList = gson.fromJson(reader, CoursesFileFormat.class);
        } catch (final JsonSyntaxException ex) {
            throw new UserVisibleException("Local course storage corrupted");
        } finally {
            try {
                reader.close();
            } catch (final IOException e) {
                return getCourses(courseList);
            }
        }

        return getCourses(courseList);
    }

    private List<Course> getCourses(final CoursesFileFormat courseList) {

        if (courseList != null) {
            return courseList.getCourses();
        } else {
            return new ArrayList<Course>();
        }
    }

    @Override
    public void save(final List<Course> courses) {

        final CoursesFileFormat courseList = new CoursesFileFormat();
        courseList.setCourses(courses);

        if (io == null) {
            throw new UserVisibleException("Could not save course data to local storage.");
        }

        final Writer writer = io.getWriter();
        if (writer == null) {
            throw new UserVisibleException("Could not save course data to local storage.");
        }

        gson.toJson(courseList, writer);
        try {
            writer.close();
        } catch (final IOException e) {
            // TODO: Log here?
            return;
        }
    }

    private Gson createGson() {

        return new GsonBuilder().serializeNulls().setPrettyPrinting().registerTypeAdapter(ExerciseKey.class, new ExerciseKey.GsonAdapter()).create();
    }

}
