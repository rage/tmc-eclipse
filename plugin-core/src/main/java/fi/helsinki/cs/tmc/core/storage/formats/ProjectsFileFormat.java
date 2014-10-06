package fi.helsinki.cs.tmc.core.storage.formats;

import fi.helsinki.cs.tmc.core.domain.Project;

import java.util.List;

public class ProjectsFileFormat {

    private List<Project> projects;

    public List<Project> getProjects() {

        return projects;
    }

    public void setProjects(final List<Project> projects) {

        this.projects = projects;
    }

}
