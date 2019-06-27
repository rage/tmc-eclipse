package fi.helsinki.cs.tmc.core.old.old.spyware.utility;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionUtils {

    public static String backtraceToString(final Throwable t) {

        final StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static void logException(final Throwable t, final Logger log, final Level level) {

        final String msg = t.getMessage() + "\n" + backtraceToString(t);
        log.log(level, msg);
    }

    public static RuntimeException toRuntimeException(final Exception ex) {

        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        } else {
            return new RuntimeException(ex);
        }
    }
}
