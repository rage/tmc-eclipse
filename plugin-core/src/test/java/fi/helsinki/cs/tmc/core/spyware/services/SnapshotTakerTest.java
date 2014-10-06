package fi.helsinki.cs.tmc.core.spyware.services;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.io.FileUtil;
import fi.helsinki.cs.tmc.core.services.ProjectDAO;
import fi.helsinki.cs.tmc.core.services.Settings;
import fi.helsinki.cs.tmc.core.spyware.ChangeType;
import fi.helsinki.cs.tmc.core.spyware.SnapshotInfo;
import fi.helsinki.cs.tmc.core.spyware.utility.ActiveThreadSet;
import fi.helsinki.cs.tmc.core.storage.DataSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SnapshotTakerTest {
    
    private static final String PROJECT_NAME = "testProject";

    private SnapshotTaker taker;
    private EventReceiver receiver;
    private DataSource<Project> source;
    private Settings settings;
    private LoggableEvent event;

    private String relPath;
    private String fullPath;

    @Before
    public void setUp() throws Exception {

        relPath = "src/test/java/fi/helsinki/cs/tmc/core/spyware/services/testProject/aaa.txt";
        fullPath = FileUtil.getUnixPath(new File(relPath).getCanonicalPath());
        createReceiver();
        createSource();

        final List<Project> sourceList = new ArrayList<Project>();
        final List<String> projectFiles = new ArrayList<String>();

        projectFiles.add(FileUtil.getUnixPath(new File("src/test/java/fi/helsinki/cs/tmc/core/spyware/services/testProject/build.xml").getPath()));
        sourceList.add(new Project(new Exercise(PROJECT_NAME, "course1"), projectFiles));
        source.save(sourceList);

        settings = mock(Settings.class);
        when(settings.isSpywareEnabled()).thenReturn(true);

        taker = new SnapshotTaker(new ActiveThreadSet(), receiver, settings, new ProjectDAO(source));
    }

    @Test
    public void receiverDoesNotReceiveEventIfSpywareIsDisabled() throws IOException, InterruptedException {

        when(settings.isSpywareEnabled()).thenReturn(false);

        taker.execute(new SnapshotInfo(PROJECT_NAME, "", relPath, "", fullPath, ChangeType.FILE_CHANGE));

        Thread.sleep(50);

        assertEquals(event, null);
    }

    @Test
    public void receiverDoesNotReceiveEventIfProjectIsNull() throws IOException, InterruptedException {

        final ProjectDAO dao = mock(ProjectDAO.class);
        when(dao.getProjectByFile(any(String.class))).thenReturn(null);
        taker = new SnapshotTaker(new ActiveThreadSet(), receiver, settings, dao);

        taker.execute(new SnapshotInfo(PROJECT_NAME, "", relPath, "", fullPath, ChangeType.FILE_CHANGE));

        Thread.sleep(50);

        assertEquals(event, null);
    }

    @Test
    public void fileChangeTest() {

        final SnapshotInfo info = new SnapshotInfo(PROJECT_NAME, "", relPath, "", fullPath, ChangeType.FILE_CHANGE);
        taker.execute(info);

        int i = 0;
        while (event == null) {
            if (i > 400) {
                fail("LoggableEvent is null after waiting over 2000ms");
            }
            i++;
            try {
                Thread.sleep(5);
            } catch (final InterruptedException e) {
            }
        }

        assertEquals(event.getCourseName(), "course1");
    }

    @Test
    public void fileRenameTest() {

        final SnapshotInfo info = new SnapshotInfo(PROJECT_NAME, relPath + "a", relPath, fullPath + "a", fullPath, ChangeType.FILE_RENAME);
        taker.execute(info);

        int i = 0;
        while (event == null) {
            if (i > 400) {
                fail("LoggableEvent is null after waiting over 2000ms");
            }
            i++;
            try {
                Thread.sleep(5);
            } catch (final InterruptedException e) {
            }
        }

        assertEquals(event.getCourseName(), "course1");
    }

    @Test
    public void folderRenameTest() {

        final SnapshotInfo info = new SnapshotInfo(PROJECT_NAME, relPath + "a", relPath, fullPath + "a", fullPath, ChangeType.FOLDER_RENAME);
        taker.execute(info);

        int i = 0;
        while (event == null) {
            if (i > 400) {
                fail("LoggableEvent is null after waiting over 2000ms");
            }
            i++;
            try {
                Thread.sleep(5);
            } catch (final InterruptedException e) {
            }
        }

        assertEquals(event.getCourseName(), "course1");
    }

    private void createReceiver() {

        receiver = new EventReceiver() {

            @Override
            public void close() throws IOException {

            }

            @Override
            public void receiveEvent(final LoggableEvent event) {

                SnapshotTakerTest.this.event = event;
            }
        };
    }

    private void createSource() {

        source = new DataSource<Project>() {

            private List<Project> list = new ArrayList<Project>();

            @Override
            public void save(final List<Project> elements) {

                list = elements;
            }

            @Override
            public List<Project> load() {

                return list;
            }
        };
    }

}
