package fi.helsinki.cs.tmc.core.old.old.spyware.services;

import fi.helsinki.cs.tmc.core.old.old.domain.Exercise;

public class LoggableEvent implements TmcEvent {

    private final String courseName;
    private final String exerciseName;
    private final String eventType;
    private final byte[] data;

    // can be null
    private final String metadata;

    // millis from epoch
    private long happenedAt;
    private final long systemNanotime;
    private transient String key;

    public LoggableEvent(final Exercise exercise,
                         final String eventType,
                         final byte[] data) {

        this(exercise, eventType, data, null);
    }

    public LoggableEvent(final Exercise exercise,
                         final String eventType,
                         final byte[] data,
                         final String metadata) {

        this(exercise.getCourseName(), exercise.getName(), eventType, data, metadata);
    }

    public LoggableEvent(final String courseName,
                         final String exerciseName,
                         final String eventType,
                         final byte[] data) {

        this(courseName, exerciseName, eventType, data, null);
    }

    public LoggableEvent(final String courseName,
                         final String exerciseName,
                         final String eventType,
                         final byte[] data,
                         final String metadata) {

        this.courseName = courseName;
        this.exerciseName = exerciseName;
        this.eventType = eventType;
        this.data = data;
        this.metadata = metadata;
        happenedAt = System.currentTimeMillis();
        systemNanotime = System.nanoTime();

        key = courseName + "|" + exerciseName + "|" + eventType;
    }

    public String getCourseName() {

        return courseName;
    }

    public String getExerciseName() {

        return exerciseName;
    }

    public String getEventType() {

        return eventType;
    }

    public byte[] getData() {

        return data;
    }

    /**
     * Optional JSON metadata.
     */
    public String getMetadata() {

        return metadata;
    }

    /**
     * {@code key = course name + "|" + exercise name + "|" + event type}.
     */
    public String getKey() {

        return key;
    }

    public long getHappenedAt() {

        return happenedAt;
    }

    public void setHappenedAt(final long happenedAt) {

        this.happenedAt = happenedAt;
    }

    public long getSystemNanotime() {

        return systemNanotime;
    }

    @Override
    public String toString() {

        return "LoggableEvent{" + "courseName=" + courseName + ", exerciseName=" + exerciseName + ", eventType=" + eventType + ", happenedAt=" +
                happenedAt + ", systemNanotime=" + systemNanotime + ", key=" + key + ", metadata=" + metadata + ", data=" + new String(data) + "}";
    }
}
