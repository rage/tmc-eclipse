package fi.helsinki.cs.tmc.core.old.io.zip.zippingdecider;

import fi.helsinki.cs.tmc.core.old.domain.Project;

/**
 * Default zipping decider that is used by Java Ant and C projects.
 */
public class DefaultZippingDecider extends AbstractZippingDecider {

    public DefaultZippingDecider(final Project project) {

        super(project);
    }

    /**
     * zips extra student files and content of the src folder.
     */
    @Override
    public boolean shouldZip(final String zipPath) {

        if (!super.shouldZip(zipPath)) {
            return false;
        }

        if (getProject().getExtraStudentFiles() != null && getProject().getExtraStudentFiles().contains(withoutRootDir(zipPath))) {
            return true;
        } else {
            return zipPath.contains("/src/");
        }

    }

    private String withoutRootDir(final String zipPath) {

        final int i = zipPath.indexOf('/');
        if (i != -1) {
            return zipPath.substring(i + 1);
        } else {
            return "";
        }
    }

}
