package fi.helsinki.cs.tmc.core.old.old.io.zip.unzippingdecider;

import fi.helsinki.cs.tmc.core.old.old.domain.Project;
import fi.helsinki.cs.tmc.core.old.old.io.IOFactory;

/**
 * Unzipping decider for java ant and C projects.
 */
public class DefaultUnzippingDecider extends AbstractUnzippingDecider {

    public DefaultUnzippingDecider(final IOFactory io, final Project project) {

        super(io, project);
    }

    /**
     * Prevents overwriting files in /src folder when unzipping so that changes
     * made by the user will not be lost.
     */
    @Override
    public boolean shouldUnzip(final String filePath) {

        final String s = getProject().getRootPath() + "/src";
        if (filePath.startsWith(s) && (filePath.equals(s) || filePath.charAt(s.length()) == '/')) {
            return !(getIo().newFile(filePath).fileExists());
        }
        return super.shouldUnzip(filePath);
    }

}
