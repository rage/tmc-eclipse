package fi.helsinki.cs.tmc.core.old.spyware.services;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.helsinki.cs.tmc.core.old.domain.Project;
import fi.helsinki.cs.tmc.core.old.domain.exception.InvalidProjectException;
import fi.helsinki.cs.tmc.core.old.io.FileIO;
import fi.helsinki.cs.tmc.core.old.io.zip.RecursiveZipper;
import fi.helsinki.cs.tmc.core.old.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.old.services.Settings;
import fi.helsinki.cs.tmc.core.old.spyware.ChangeType;
import fi.helsinki.cs.tmc.core.old.spyware.SnapshotInfo;
import fi.helsinki.cs.tmc.core.old.spyware.utility.ActiveThreadSet;
import fi.helsinki.cs.tmc.core.old.spyware.utility.JsonMaker;

public class SnapshotTaker {

    private static final Logger LOG = Logger.getLogger(SnapshotTaker.class.getName());

    private SnapshotInfo info;
    private final ActiveThreadSet threadSet;
    private final EventReceiver receiver;
    private final Settings settings;
    private final ProjectDAO projectDAO;

    public SnapshotTaker(final ActiveThreadSet threadSet,
                         final EventReceiver receiver,
                         final Settings settings,
                         final ProjectDAO projectDAO) {

        this.threadSet = threadSet;
        this.receiver = receiver;
        this.settings = settings;
        this.projectDAO = projectDAO;
    }

    public void execute(final SnapshotInfo info) {

        this.info = info;

        if (info.getChangeType() == ChangeType.FILE_RENAME || info.getChangeType() == ChangeType.FOLDER_RENAME) {
            handleRename();
        } else {
            handleChange();
        }
    }

    private void handleChange() {

        final String metadata = JsonMaker.create().add("cause", info.getChangeType().name().toLowerCase()).add("file", info.getCurrentFilePath())
                .toString();

        startSnapshotThread(metadata, info.getCurrentFullFilePath());

    }

    private void handleRename() {

        final String metadata = JsonMaker.create().add("cause", info.getChangeType().name().toLowerCase()).add("file", info.getCurrentFilePath())
                .add("previous_name", info.getOldFilePath()).toString();

        startSnapshotThread(metadata, info.getOldFullFilePath());

    }

    private void startSnapshotThread(final String metadata, final String path) {

        if (!settings.isSpywareEnabled()) {
            return;
        }

        final Project project = projectDAO.getProjectByFile(path);

        // Note: Should *only* log TMC courses.
        if (project == null) {
            return;
        }

        final SnapshotThread thread = new SnapshotThread(receiver, project, metadata);
        threadSet.addThread(thread);
        thread.setDaemon(true);
        thread.start();

    }

    private static final class SnapshotThread extends Thread {

        private final EventReceiver receiver;
        private final Project project;
        private final String metadata;

        private SnapshotThread(final EventReceiver receiver, final Project project, final String metadata) {

            super("Source snapshot");
            this.receiver = receiver;
            this.project = project;
            this.metadata = metadata;
        }

        @Override
        public void run() {

            // Note note: the following note is from the original netbeans code

            // Note that, being in a thread, this is inherently prone to races
            // that modify the project. For now we just accept that. Not sure if
            // the File Object API would allow some sort of global locking of
            // the project.
            final RecursiveZipper zipper;
            try {
                zipper = new RecursiveZipper(new FileIO(project.getRootPath()), project.getZippingDecider());
            } catch (final InvalidProjectException e) {
                // this exception is thrown when file list is empty
                return;
            }
            try {
                final byte[] data = zipper.zipProjectSources();
                final LoggableEvent event = new LoggableEvent(project.getExercise(), "code_snapshot", data, metadata);
                receiver.receiveEvent(event);
            } catch (final IOException ex) {
                // Warning might be also appropriate, but this often races with
                // project closing during integration tests, and there warning
                // would cause a dialog to appear, failing the test.
                LOG.log(Level.INFO, "Error zipping project sources in: " + project.getRootPath(), ex);
            }

        }
    }

}
