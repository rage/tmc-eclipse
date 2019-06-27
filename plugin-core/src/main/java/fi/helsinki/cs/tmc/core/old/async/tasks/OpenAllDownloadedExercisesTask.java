package fi.helsinki.cs.tmc.core.old.async.tasks;

import java.util.ArrayList;
import java.util.List;

import fi.helsinki.cs.tmc.core.old.async.SimpleBackgroundTask;
import fi.helsinki.cs.tmc.core.old.domain.Exercise;
import fi.helsinki.cs.tmc.core.old.domain.ProjectStatus;
import fi.helsinki.cs.tmc.core.old.io.FileIO;
import fi.helsinki.cs.tmc.core.old.io.IOFactory;
import fi.helsinki.cs.tmc.core.old.services.ProjectOpener;

public class OpenAllDownloadedExercisesTask extends SimpleBackgroundTask<Exercise> {

    private final ProjectOpener opener;
    private final IOFactory io;

    public OpenAllDownloadedExercisesTask(final String description, final List<Exercise> list, final ProjectOpener opener, final IOFactory io) {

        super(description, list);
        this.opener = opener;
        this.io = io;
    }

    @Override
    public void run(final Exercise exercise) {

        if (canOpen(exercise)) {
            opener.open(exercise);
            exercise.getProject().setStatus(ProjectStatus.DOWNLOADED);
        }
    }

    private boolean canOpen(final Exercise exercise) {

        if (exercise.getProject().getReadOnlyProjectFiles().isEmpty()) {
            return false;
        }

        final FileIO root = io.newFile(exercise.getProject().getRootPath());
        final FileIO buildFile = io.newFile(root.getPath() + "/" + exercise.getProject().getProjectType().getBuildFile());

        if (buildFile.fileExists()) {
            return true;
        } else {
            cleanup(exercise);
            return false;
        }
    }

    private void cleanup(final Exercise exercise) {

        exercise.getProject().setProjectFiles(new ArrayList<String>());
    }

}
