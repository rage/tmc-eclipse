package fi.helsinki.cs.tmc.core.old.spyware;

public class DocumentInfo {

    private final String fullPath;
    private final String relativePath;
    private final String editorText;
    private final String eventText;
    private final int offset;
    private final int length;

    /**
     * Helper class that carries needed info for logging text change events. If
     * editorText is empty (whitespace is not empty!), it is considered to be an
     * deletion event
     */
    public DocumentInfo(final String fullPath, final String relativePath, final String editorText, final String eventText, final int offset,
            final int length) {

        this.editorText = editorText;
        this.eventText = eventText;
        this.fullPath = fullPath;
        this.relativePath = relativePath;
        this.offset = offset;
        this.length = length;
    }

    public String getFullPath() {

        return fullPath;
    }

    public String getRelativePath() {

        return relativePath;
    }

    public String getEditorText() {

        return editorText;
    }

    public String getEventText() {

        return eventText;
    }

    public int getOffset() {

        return offset;
    }

    public int getLength() {

        return length;
    }

}
