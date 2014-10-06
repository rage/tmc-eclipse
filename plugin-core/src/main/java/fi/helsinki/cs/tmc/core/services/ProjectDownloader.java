package fi.helsinki.cs.tmc.core.services;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.ZippedProject;
import fi.helsinki.cs.tmc.core.services.http.ServerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles project downloading. Used by the DownloaderTask background
 * task.
 */
public class ProjectDownloader {

    private final ServerManager server;

    public ProjectDownloader(final ServerManager server) {

        this.server = server;
    }

    public List<ZippedProject> downloadExercises(final List<Exercise> exercises) {

        final List<ZippedProject> projects = new ArrayList<ZippedProject>();
        for (final Exercise exercise : exercises) {
            projects.add(downloadExercise(exercise));
        }
        return projects;
    }

    public ZippedProject downloadExercise(final Exercise exercise) {

        final String zipUrl = exercise.getDownloadUrl();
        return server.getExerciseZip(zipUrl);
    }

}
