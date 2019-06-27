package fi.helsinki.cs.tmc.core.old.old.io;

/**
 * An interface for IO creation; mostly used to enable better unit testing as we
 * can inject mock factory that creates mock objects.
 */
public interface IOFactory {

    FileIO newFile(String path);

}
