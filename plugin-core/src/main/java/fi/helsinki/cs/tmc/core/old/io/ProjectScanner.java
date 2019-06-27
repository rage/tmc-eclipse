package fi.helsinki.cs.tmc.core.old.old.io;

import java.util.ArrayList;
import java.util.List;

import fi.helsinki.cs.tmc.core.old.old.domain.Project;
import fi.helsinki.cs.tmc.core.old.old.domain.ProjectStatus;
import fi.helsinki.cs.tmc.core.old.old.services.ProjectDAO;

/**
 * Class that is used on startup to ensure that the project database remains in
 * consistent state even if files are deleted or added on disk while IDE is
 * closed.
 */
public class ProjectScanner {

    private final ProjectDAO projectDAO;
    private final IOFactory io;

    public ProjectScanner(final ProjectDAO projectDAO, final IOFactory io) {

        this.projectDAO = projectDAO;
        this.io = io;
    }

    public void updateProject(final Project project) {

        if (project.getStatus() == ProjectStatus.DELETED) {
            return;
        }

        final List<String> files = new ArrayList<String>();
        traverse(files, io.newFile(project.getRootPath()));

        project.setProjectFiles(files);

        if (project.existsOnDisk()) {
            project.setStatus(ProjectStatus.DOWNLOADED);
        } else {
            project.setStatus(ProjectStatus.NOT_DOWNLOADED);
        }
    }

    public void updateProjects() {

        for (final Project project : projectDAO.getProjects()) {
            updateProject(project);
        }
    }

    private void traverse(final List<String> list, final FileIO file) {

        if (file != null && (file.fileExists() || file.directoryExists())) {
            list.add(file.getPath());

            if (file.directoryExists()) {
                for (final FileIO child : file.getChildren()) {
                    traverse(list, child);
                }
            }
        }
    }

}
