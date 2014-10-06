package fi.helsinki.cs.tmc.core.domain;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import fi.helsinki.cs.tmc.core.domain.ExerciseKey.GsonAdapter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ExerciseKeyTest {

    private static final String COURSE = "course";
    private static final String EXERCISE = "exercise";
    
    private ExerciseKey ek;

    @Before
    public void setUp() {

        ek = new ExerciseKey(COURSE, EXERCISE);
    }

    @Test
    public void constructorSetMemberVariables() {

        final ExerciseKey e = new ExerciseKey(COURSE, EXERCISE);
        assertEquals(COURSE, e.getCourseName());
        assertEquals(EXERCISE, e.getExerciseName());
    }

    @Test
    public void equalityIfCourseAndExerciseAreSame() {

        final ExerciseKey same = new ExerciseKey(COURSE, EXERCISE);
        assertEquals(ek, same);
    }

    @Test
    public void notEqualIfCourseOrExerciseIsNotSame() {

        final ExerciseKey other1 = new ExerciseKey(COURSE, "c");
        final ExerciseKey other2 = new ExerciseKey("c", EXERCISE);
        assertFalse(ek.equals(other1));
        assertFalse(ek.equals(other2));
    }

    @Test
    public void notEqualOnNull() {

        assertFalse(ek.equals(null));
    }

    @Test
    public void notEqualWithOtherClasses() {

        assertFalse(ek.equals(new Integer(1)));
    }

    @Test
    public void hashCodeIsSameIfObjectAreEqual() {

        final ExerciseKey same = new ExerciseKey(COURSE, EXERCISE);
        assertEquals(ek.hashCode(), same.hashCode());
    }

    @Test
    public void hashcodeNotSameIfCourseOrExerciseIsNotSame() {

        final ExerciseKey other1 = new ExerciseKey(COURSE, "c");
        final ExerciseKey other2 = new ExerciseKey("c", EXERCISE);
        assertFalse(ek.hashCode() == other1.hashCode());
        assertFalse(ek.hashCode() == other2.hashCode());
    }

    @Test
    public void toStringReturnCorrectString() {

        assertEquals("course/exercise", ek.toString());
    }

    /*
     * GSONADAPTER
     */

    @Test
    public void gsonAdapterReturnEqualObjectAfterSerializationAndDeserialization() {

        final GsonAdapter adapter = new GsonAdapter();
        final JsonElement element = adapter.serialize(ek, getClass(), null);
        final ExerciseKey deserializedKey = adapter.deserialize(element, getClass(), null);

        assertEquals(ek, deserializedKey);
    }

    @Test
    public void testGsonAdapterSerialize() {

        final ExerciseKey.GsonAdapter ga = new ExerciseKey.GsonAdapter();
        assertEquals(ga.serialize(ek, ek.getClass(), null), new JsonPrimitive("course/exercise"));
    }

    @Test
    public void testGsonAdapterDeserialize() {

        final ExerciseKey.GsonAdapter ga = new ExerciseKey.GsonAdapter();
        final JsonPrimitive jp = new JsonPrimitive("course/exercise");
        assertEquals(ga.deserialize(jp, jp.getClass(), null), ek);
    }

    @Test(expected = JsonParseException.class)
    public void testGsonAdapterDeserializeFailure() throws JsonParseException {

        final ExerciseKey.GsonAdapter ga = new ExerciseKey.GsonAdapter();
        final JsonPrimitive jp = new JsonPrimitive("aa");
        ga.deserialize(jp, jp.getClass(), null);
    }

    @Test(expected = JsonParseException.class)
    public void testGsonAdapterDeserializeFailureEmptyString() throws JsonParseException {

        final ExerciseKey.GsonAdapter ga = new ExerciseKey.GsonAdapter();
        final JsonPrimitive jp = new JsonPrimitive("");
        ga.deserialize(jp, jp.getClass(), null);
    }
}
