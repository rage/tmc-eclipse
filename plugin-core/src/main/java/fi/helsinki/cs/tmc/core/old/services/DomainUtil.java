package fi.helsinki.cs.tmc.core.old.services;

import java.util.ArrayList;
import java.util.List;

import fi.helsinki.cs.tmc.core.old.domain.Course;

/**
 * Helper class that gets course names from given courses.
 */
public class DomainUtil {

    public static String[] getCourseNames(final List<Course> courses) {

        final List<String> courseNames = new ArrayList<String>();
        for (final Course c : courses) {
            courseNames.add(c.getName());
        }
        return courseNames.toArray(new String[0]);
    }

}
