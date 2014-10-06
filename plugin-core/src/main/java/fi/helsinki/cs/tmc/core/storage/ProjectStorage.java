package fi.helsinki.cs.tmc.core.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.io.FileIO;
import fi.helsinki.cs.tmc.core.storage.formats.ProjectsFileFormat;
import fi.helsinki.cs.tmc.core.ui.UserVisibleException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class ProjectStorage implements DataSource<Project> {

    private final FileIO io;
    private final Gson gson;

    public ProjectStorage(final FileIO io) {

        this.io = io;
        gson = createGson();
    }

    @Override
    public List<Project> load() {

        if (!io.fileExists()) {
            return new ArrayList<Project>();
        }
        ProjectsFileFormat projectList = null;
        final Reader reader = io.getReader();
        if (reader == null) {
            throw new UserVisibleException("Could not load project data from local storage.");
        }
        try {
            projectList = gson.fromJson(reader, ProjectsFileFormat.class);
        } catch (final JsonSyntaxException ex) {
            throw new UserVisibleException("Local project storage corrupted");
        } finally {
            try {
                reader.close();
            } catch (final IOException e) {
                return getProjectList(projectList);
            }
        }

        return getProjectList(projectList);
    }

    private List<Project> getProjectList(final ProjectsFileFormat projectList) {

        if (projectList != null) {
            return projectList.getProjects();
        } else {
            return new ArrayList<Project>();
        }
    }

    @Override
    public void save(final List<Project> projects) {

        final ProjectsFileFormat projectList = new ProjectsFileFormat();
        projectList.setProjects(projects);

        if (io == null) {
            throw new UserVisibleException("Could not save project data to local storage.");
        }

        final Writer writer = io.getWriter();
        if (writer == null) {
            throw new UserVisibleException("Could not save project data to local storage.");
        }

        gson.toJson(projectList, writer);
        try {
            writer.close();
        } catch (final IOException e) {
            // TODO: Log here?
            return;
        }
    }

    private Gson createGson() {

        return new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    }

}
