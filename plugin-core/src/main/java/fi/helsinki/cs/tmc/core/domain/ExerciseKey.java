package fi.helsinki.cs.tmc.core.domain;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A pair (course name, exercise name).
 */
public final class ExerciseKey {

    private final String courseName;
    private final String exerciseName;

    public ExerciseKey(final String courseName, final String exerciseName) {

        this.courseName = courseName;
        this.exerciseName = exerciseName;
    }

    public String getCourseName() {

        return courseName;
    }

    public String getExerciseName() {

        return exerciseName;
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj instanceof ExerciseKey) {
            final ExerciseKey that = (ExerciseKey) obj;
            return Objects.equals(courseName, that.courseName) && Objects.equals(exerciseName, that.exerciseName);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {

        return Objects.hash(courseName, exerciseName);
    }

    @Override
    public String toString() {

        return courseName + "/" + exerciseName;
    }

    public static class GsonAdapter implements JsonSerializer<ExerciseKey>, JsonDeserializer<ExerciseKey> {

        @Override
        public JsonElement serialize(final ExerciseKey key, final Type type, final JsonSerializationContext jsc) {

            return new JsonPrimitive(key.toString());
        }

        @Override
        public ExerciseKey deserialize(final JsonElement je, final Type type, final JsonDeserializationContext jdc) {

            final String[] parts = je.getAsString().split("/", 2);
            if (parts.length != 2) {
                throw new JsonParseException("Invalid ExerciseKey representation: \"" + je.getAsString() + "\"");
            }
            return new ExerciseKey(parts[0], parts[1]);
        }
    }
}
