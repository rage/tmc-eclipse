package fi.helsinki.cs.tmc.core.old.services.http;

public enum UrlExtension {
    COURSES("courses.json"), EXERCISES("courses/");

    private String extension;

    private UrlExtension(final String extension) {

        this.extension = extension;
    }

    public String getExtension() {

        return extension;
    }

    public String getExtension(final String id) {

        return extension + id + ".json";
    }
}
