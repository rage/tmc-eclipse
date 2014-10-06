package fi.helsinki.cs.tmc.core.spyware.services;

import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.services.Settings;
import fi.helsinki.cs.tmc.core.spyware.DocumentInfo;
import fi.helsinki.cs.tmc.core.spyware.async.DocumentSendThread;
import fi.helsinki.cs.tmc.core.spyware.utility.ActiveThreadSet;
import fi.helsinki.cs.tmc.core.spyware.utility.diff_match_patch;

import java.util.HashMap;
import java.util.Map;

/**
 * This class handles text inserts, removals and cut\pastes. It uses information
 * it receives from the plugin.
 */
public class DocumentChangeHandler {

    private static final diff_match_patch PATCH_GENERATOR = new diff_match_patch();
    private final EventReceiver receiver;
    private final Map<String, String> documentCache;
    private final ActiveThreadSet activeThreads;
    private final Settings settings;
    private final ProjectDAO projectDAO;

    public DocumentChangeHandler(final EventReceiver receiver, final ActiveThreadSet set, final Settings settings, final ProjectDAO projectDAO) {

        this.receiver = receiver;
        activeThreads = set;
        documentCache = new HashMap<String, String>();
        this.settings = settings;
        this.projectDAO = projectDAO;
    }

    public void handleEvent(final DocumentInfo info) {

        if (!settings.isSpywareEnabled()) {
            return;
        }

        final Project project = projectDAO.getProjectByFile(info.getFullPath());

        if (project == null) {
            return;
        }

        final DocumentSendThread thread = new DocumentSendThread(receiver, info, project, documentCache, PATCH_GENERATOR);
        activeThreads.addThread(thread);
        thread.setDaemon(true);
        thread.start();
    }
}
