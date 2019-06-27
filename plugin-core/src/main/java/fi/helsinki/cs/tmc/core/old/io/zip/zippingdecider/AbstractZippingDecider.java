package fi.helsinki.cs.tmc.core.old.io.zip.zippingdecider;

import java.io.File;

import fi.helsinki.cs.tmc.core.old.domain.Project;

/**
 * Abstract base class for all zipping deciders.
 */
public abstract class AbstractZippingDecider implements ZippingDecider {

    private Project project;

    public AbstractZippingDecider(final Project project) {

        this.project = project;
    }

    protected Project getProject() {

        return this.project;
    }

    protected void setProject(final Project project) {

        this.project = project;
    }

    /**
     * Does not include any folders that contain .tmcnosubmit-file.
     */
    @Override
    public boolean shouldZip(final String zipPath) {

        final File dir = new File(new File(project.getRootPath()).getParentFile(), zipPath);
        if (!dir.isDirectory()) {
            return true;
        }

        return !new File(dir, ".tmcnosubmit").exists();

    }
}
