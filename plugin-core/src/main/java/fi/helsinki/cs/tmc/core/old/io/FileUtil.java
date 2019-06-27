package fi.helsinki.cs.tmc.core.old.io;

import java.io.File;

/**
 * Helper class for file operations.
 */
public class FileUtil {

    public static String append(final String a, final String b) {

        return getUnixPath(a) + "/" + getUnixPath(b);
    }

    /**
     * Replaces the file separators in given path with Unix styled separators.
     * We use these internally.
     */
    public static String getUnixPath(final String path) {

        String unixPath = path.replace(File.separator, "/");

        if (!unixPath.isEmpty() && unixPath.charAt(unixPath.length() - 1) == '/') {
            unixPath = unixPath.substring(0, unixPath.length() - 1);
        }

        return unixPath;
    }

    public static String getNativePath(final String path) {

        return new File(path).getAbsolutePath();
    }

}
