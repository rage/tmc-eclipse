package fi.helsinki.cs.tmc.core.old.io.zip.unzippingdecider;

import java.util.List;

import fi.helsinki.cs.tmc.core.old.domain.Project;
import fi.helsinki.cs.tmc.core.old.io.IOFactory;

/**
 * Abstract base class that provides common functionality for all unzipping
 * deciders.
 */
public abstract class AbstractUnzippingDecider implements UnzippingDecider {

    private Project project;
    private final List<String> doNotUnzip;
    private IOFactory io;

    public AbstractUnzippingDecider(final IOFactory io, final Project project) {

        this.project = project;
        this.setIo(io);
        doNotUnzip = new TmcProjectFile(io.newFile(project.getRootPath() + "/.tmcproject.yml")).getExtraStudentFiles();
    }

    protected Project getProject() {

        return this.project;
    }

    protected void setProject(final Project project) {

        this.project = project;
    }

    protected IOFactory getIo() {

        return this.io;
    }

    protected void setIo(final IOFactory io) {

        this.io = io;
    }

    /**
     * Prevents unzipping if file would overwrite file on the extra student files list.
     */
    @Override
    public boolean shouldUnzip(final String filePath) {

        for (String path : doNotUnzip) {

            String noUnzipPath = path;

            if (noUnzipPath.charAt(noUnzipPath.length() - 1) == '/') {
                noUnzipPath = noUnzipPath.substring(0, noUnzipPath.length() - 1);
            }

            noUnzipPath = project.getRootPath() + "/" + noUnzipPath;

            if (filePath.startsWith(noUnzipPath) && (filePath.equals(noUnzipPath) || filePath.charAt(noUnzipPath.length()) == '/')) {
                return false;
            }
        }
        return true;
    }
}
