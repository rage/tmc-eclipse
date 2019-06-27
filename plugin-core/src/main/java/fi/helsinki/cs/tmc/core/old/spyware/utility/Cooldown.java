package fi.helsinki.cs.tmc.core.old.spyware.utility;

public class Cooldown {

    private long durationMillis;
    private long startTime;

    public Cooldown(final long durationMillis) {

        this.durationMillis = durationMillis;
        startTime = 0;
    }

    public long getDurationMillis() {

        return durationMillis;
    }

    public void setDurationMillis(final long durationMillis) {

        this.durationMillis = durationMillis;
    }

    public void start() {

        startTime = System.currentTimeMillis();
    }

    public boolean isExpired() {

        return System.currentTimeMillis() >= startTime + durationMillis;
    }
}
