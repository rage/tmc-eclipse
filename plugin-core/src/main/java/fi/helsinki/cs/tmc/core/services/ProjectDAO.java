package fi.helsinki.cs.tmc.core.services;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.domain.ProjectStatus;
import fi.helsinki.cs.tmc.core.storage.DataSource;

import java.util.List;

/**
 * Class that handles Project object storage, loading and saving.
 */
public class ProjectDAO {

    private final DataSource<Project> dataSource;
    private List<Project> projects;

    public ProjectDAO(final DataSource<Project> dataSource) {

        this.dataSource = dataSource;
        loadProjects();
    }

    public void loadProjects() {

        projects = dataSource.load();
    }

    public List<Project> getProjects() {

        return projects;
    }

    public void setProjects(final List<Project> projects) {

        this.projects = projects;
    }

    public void addProject(final Project project) {

        if (projects.contains(project)) {
            projects.remove(project);
        }
        projects.add(project);
        save();
    }

    public Project getProjectByFile(final String filePath) {

        for (final Project project : projects) {
            if (project.containsFile(filePath) && project.getStatus() != ProjectStatus.DELETED) {
                return project;
            }
        }
        return null;
    }

    public Project getProjectByExercise(final Exercise exercise) {

        for (final Project project : projects) {
            if (project.getExercise().equals(exercise)) {
                return project;
            }
        }
        return null;
    }

    public void save() {

        dataSource.save(projects);
    }

}
