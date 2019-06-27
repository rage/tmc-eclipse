package fi.helsinki.cs.tmc.core.old.old.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import fi.helsinki.cs.tmc.core.old.old.domain.SubmissionResult;
import fi.helsinki.cs.tmc.testrunner.StackTraceSerializer;

import java.lang.reflect.Type;

public class SubmissionResultParser {

    private final Gson gson = new GsonBuilder().registerTypeAdapter(SubmissionResult.Status.class, new StatusDeserializer())
            .registerTypeAdapter(StackTraceElement.class, new StackTraceSerializer()).create();

    public SubmissionResult parseFromJson(final String json) {

        if (json.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty input");
        }

        try {
            return gson.fromJson(json, SubmissionResult.class);
        } catch (final JsonSyntaxException e) {
            throw new RuntimeException("Failed to parse submission result: " + e.getMessage(), e);
        }
    }

    private static class StatusDeserializer implements JsonDeserializer<SubmissionResult.Status> {

        @Override
        public SubmissionResult.Status deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {

            final String element = json.getAsJsonPrimitive().getAsString();

            try {
                return SubmissionResult.Status.valueOf(element.toUpperCase());
            } catch (final IllegalArgumentException e) {
                throw new JsonParseException("Unknown submission status: " + element);
            }
        }
    }
}
