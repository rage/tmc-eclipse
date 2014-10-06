package fi.helsinki.cs.tmc.core.utils.jsonhelpers;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CourseListTest {

    @Test
    public void canDeserializeInto() {

        final CourseList courseList = new Gson().fromJson("{\"api_version\": \"7.0\", \"courses\": []}", CourseList.class);
        assertEquals("7.0", courseList.getApiVersion());
        assertEquals(0, courseList.getCourses().length);
    }

}
