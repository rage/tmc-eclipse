package fi.helsinki.cs.tmc.core.old.spyware;

/**
 * Helper class that contains necessary data for core to take a snapshot. IDE
 * must fill in this information.
 */
public class SnapshotInfo {

    private final String projectName;

    // empty string unless rename operation,
    // then it contains old path
    private final String oldFilePath;

    private final String currentFilePath;
    private final String oldFullFilePath;
    private final String currentFullFilePath;
    private final ChangeType type;

    public SnapshotInfo(final String projectName,
                        final String oldFilePath,
                        final String currentFilePath,
                        final String oldFullFilePath,
                        final String fullFilePath,
                        final ChangeType type) {

        this.projectName = projectName;
        this.oldFilePath = oldFilePath;
        this.currentFilePath = currentFilePath;
        this.type = type;
        this.oldFullFilePath = oldFullFilePath;
        this.currentFullFilePath = fullFilePath;
    }

    public String getProjectName() {

        return projectName;
    }

    public String getOldFilePath() {

        return oldFilePath;
    }

    public String getCurrentFullFilePath() {

        return currentFullFilePath;
    }

    public String getOldFullFilePath() {

        return oldFullFilePath;
    }

    public String getCurrentFilePath() {

        return currentFilePath;
    }

    public ChangeType getChangeType() {

        return type;
    }

    public boolean pathsAreEmpty() {

        return isEmpty(oldFilePath) && isEmpty(oldFullFilePath) && isEmpty(currentFilePath) && isEmpty(currentFullFilePath);
    }

    private boolean isEmpty(final String path) {

        return path == null || path.isEmpty();
    }

    @Override
    public String toString() {

        return "\tProjectname: " + projectName + "\n\tOld file path: " + oldFilePath + "\n\tCurrent file path: " + currentFilePath +
                "\n\tFull old path: " + oldFullFilePath + "\n\tFull current path: " + currentFullFilePath + "\n\tType: " + type;
    }
}
