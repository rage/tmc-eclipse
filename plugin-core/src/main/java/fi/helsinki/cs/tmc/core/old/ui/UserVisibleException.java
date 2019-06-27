package fi.helsinki.cs.tmc.core.old.old.ui;

public class UserVisibleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserVisibleException(final String msg) {

        super(msg);
    }

    public UserVisibleException(final String msg, final Throwable cause) {

        super(msg, cause);
    }
}
