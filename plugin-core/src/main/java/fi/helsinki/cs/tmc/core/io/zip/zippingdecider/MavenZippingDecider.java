package fi.helsinki.cs.tmc.core.io.zip.zippingdecider;

import fi.helsinki.cs.tmc.core.domain.Project;

import java.util.regex.Pattern;

/**
 * Zipping decider for Maven projects.
 */
public class MavenZippingDecider extends AbstractZippingDecider {

    private static final Pattern REJECT_PATTERN = Pattern.compile("^[^/]+/(target|lib/testrunner)/.*");

    public MavenZippingDecider(final Project project) {

        super(project);
    }

    /**
     * Prevents zipping any files that matches the REJECT_PATTERN regex.
     */
    @Override
    public boolean shouldZip(final String zipPath) {

        if (!super.shouldZip(zipPath)) {
            return false;
        }

        return !REJECT_PATTERN.matcher(zipPath).matches();
    }
}
