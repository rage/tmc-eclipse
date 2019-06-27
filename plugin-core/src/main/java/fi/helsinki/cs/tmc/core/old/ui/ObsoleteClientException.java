package fi.helsinki.cs.tmc.core.old.old.ui;

public class ObsoleteClientException extends UserVisibleException {

    private static final long serialVersionUID = 1L;

    public ObsoleteClientException() {

        super("Please update the TMC plugin.\nUse Help -> Check for Updates.");
    }
}
