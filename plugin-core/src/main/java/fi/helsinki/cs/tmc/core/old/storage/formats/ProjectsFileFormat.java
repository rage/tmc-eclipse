package fi.helsinki.cs.tmc.core.old.storage.formats;

import java.util.List;

import fi.helsinki.cs.tmc.core.old.domain.Project;

public class ProjectsFileFormat {

    private List<Project> projects;

    public List<Project> getProjects() {

        return projects;
    }

    public void setProjects(final List<Project> projects) {

        this.projects = projects;
    }

}
