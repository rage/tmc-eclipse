package fi.helsinki.cs.tmc.core.services.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UrlExtensionTest {

    @Test
    public void coursesReturnsCorrectUrl() {

        assertEquals("courses.json", UrlExtension.COURSES.getExtension());
    }

    @Test
    public void exercisesReturnsCorrectUrl() {

        assertEquals("courses/1.json", UrlExtension.EXERCISES.getExtension("1"));
    }

}
