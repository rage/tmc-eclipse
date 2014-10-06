package fi.helsinki.cs.tmc.core.services;

import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.domain.ProjectStatus;
import fi.helsinki.cs.tmc.core.spyware.ChangeType;
import fi.helsinki.cs.tmc.core.spyware.SnapshotInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that responds to events such as renames, deletes and additions. Ensures
 * that project database remains consistent.
 */
public class ProjectEventHandler {

    private final ProjectDAO projectDAO;

    public ProjectEventHandler(final ProjectDAO projectDAO) {

        this.projectDAO = projectDAO;
    }

    public void handleDeletion(final String projectPath) {

        final Project project = projectDAO.getProjectByFile(projectPath);
        if (project == null) {
            return;
        }

        project.setStatus(ProjectStatus.DELETED);
    }

    public void handleSnapshot(final SnapshotInfo snapshot) {

        final Project project = findProject(snapshot);

        if (project == null) {
            return;
        }

        switch (snapshot.getChangeType()) {

            case FILE_CREATE:
            case FOLDER_CREATE:
                handleCreate(project, snapshot);
                break;

            case FILE_DELETE:
                handleFileDelete(project, snapshot);
                break;

            case FOLDER_DELETE:
                handleFolderDelete(project, snapshot);
                break;

            case FILE_RENAME:
                handleFileRename(project, snapshot);
                break;

            case FOLDER_RENAME:
                handleFolderRename(project, snapshot);
                break;

            default:
                break;

        }

        if (project.existsOnDisk()) {
            project.setStatus(ProjectStatus.DOWNLOADED);
        } else {
            project.setStatus(ProjectStatus.NOT_DOWNLOADED);
        }
    }

    private Project findProject(final SnapshotInfo snapshot) {

        Project project;

        if (snapshot.getChangeType() == ChangeType.FILE_RENAME || snapshot.getChangeType() == ChangeType.FOLDER_RENAME) {
            project = projectDAO.getProjectByFile(snapshot.getOldFullFilePath());
        } else {
            project = projectDAO.getProjectByFile(snapshot.getCurrentFullFilePath());
        }

        if (project != null) {
            return project;
        }

        for (final Project p : projectDAO.getProjects()) {
            if (!p.getRootPath().isEmpty() && snapshot.getCurrentFullFilePath().startsWith(p.getRootPath())) {
                return p;
            }
        }

        return null;
    }

    private void handleFolderRename(final Project project, final SnapshotInfo snapshot) {

        if (isProjectRename(snapshot)) {
            return;
        }

        final List<String> children = getChildren(project, snapshot.getOldFullFilePath());
        for (final String file : children) {
            remove(project, file);
            add(project, file.replace(snapshot.getOldFullFilePath(), snapshot.getCurrentFullFilePath()));
        }
        remove(project, snapshot.getOldFullFilePath());
        add(project, snapshot.getCurrentFullFilePath());
    }

    private boolean isProjectRename(final SnapshotInfo snapshot) {

        return snapshot.getOldFullFilePath().isEmpty();
    }

    private void handleFileRename(final Project project, final SnapshotInfo snapshot) {

        remove(project, snapshot.getOldFullFilePath());
        add(project, snapshot.getCurrentFullFilePath());
    }

    private void handleFolderDelete(final Project project, final SnapshotInfo snapshot) {

        for (final String file : getChildren(project, snapshot.getCurrentFullFilePath())) {
            remove(project, file);
        }
        remove(project, snapshot.getCurrentFullFilePath());
    }

    private void handleFileDelete(final Project project, final SnapshotInfo snapshot) {

        remove(project, snapshot.getCurrentFullFilePath());
    }

    private void handleCreate(final Project project, final SnapshotInfo snapshot) {

        add(project, snapshot.getCurrentFullFilePath());
    }

    private void add(final Project project, final String path) {

        project.addProjectFile(path);
    }

    private void remove(final Project project, final String path) {

        project.removeProjectFile(path);
    }

    private List<String> getChildren(final Project project, final String path) {

        final List<String> files = new ArrayList<String>();
        final List<String> projectFiles = project.getReadOnlyProjectFiles();
        for (final String file : projectFiles) {
            if (file.startsWith(path + "/")) {
                files.add(file);
            }
        }
        return files;
    }

}
