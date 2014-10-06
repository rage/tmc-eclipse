package fi.helsinki.cs.tmc.core.services;

import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.domain.ProjectStatus;
import fi.helsinki.cs.tmc.core.spyware.ChangeType;
import fi.helsinki.cs.tmc.core.spyware.SnapshotInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ProjectEventHandlerTest {
    
    private static final String VALID_NEW_FILE = "testCourse/testExercise1/newFile";
    private static final String INVALID_NEW_FILE = "testCourse2/testExercise2/newFile2";

    private static final String EXISTING_FILE = "testCourse/testExercise1/existingFile";
    private static final String RENAMED_EXISTING_FILE = "testCourse/testExercise2/existingFile";

    private static final String EXISTING_FOLDER = "testCourse/testExercise1";
    private static final String RENAMED_FOLDER = "testCourse/testExercise2";

    private static final String VALID_NEW_FOLDER = "testCourse/testExercise1/folder";
    private static final String INVALID_NEW_FOLDER = "testCourse2/testExercise2/testfolder";
    
    private static final String PROJECT = "project";

    private Project project;
    private ProjectDAO projectDAO;

    private ProjectEventHandler handler;

    @Before
    public void setUp() {

        projectDAO = mock(ProjectDAO.class);
        project = mock(Project.class);

        final List<String> testExercise1Files = new ArrayList<String>();
        testExercise1Files.add(EXISTING_FILE);
        testExercise1Files.add(EXISTING_FOLDER);

        when(project.getReadOnlyProjectFiles()).thenReturn(testExercise1Files);

        when(projectDAO.getProjectByFile(EXISTING_FILE)).thenReturn(project);
        when(projectDAO.getProjectByFile(EXISTING_FOLDER)).thenReturn(project);

        when(project.getReadOnlyProjectFiles()).thenReturn(Collections.unmodifiableList(testExercise1Files));

        when(project.containsFile("testCourse/testExercise1/nonExistingFile")).thenReturn(false);
        when(project.containsFile(EXISTING_FILE)).thenReturn(true);

        when(project.getRootPath()).thenReturn("testCourse/testExercise1");

        final List<Project> projects = new ArrayList<Project>();
        projects.add(project);
        when(projectDAO.getProjects()).thenReturn(projects);

        handler = new ProjectEventHandler(projectDAO);
    }

    @Test
    public void testHandleSnapshotRenameWithInvalidFile() {

        final SnapshotInfo snapshot = new SnapshotInfo(PROJECT, "", "", "testCourse/testExercise1/nonExistingFile", "", ChangeType.FILE_RENAME);

        handler.handleSnapshot(snapshot);

        verify(projectDAO, times(1)).getProjectByFile("testCourse/testExercise1/nonExistingFile");
        verify(projectDAO, times(1)).getProjects();

        verify(project, never()).getReadOnlyProjectFiles();
        verify(project, never()).setProjectFiles(any(List.class));
    }

    @Test
    public void testHandleDeletionWithValidProject() {

        Mockito.doAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {

                final List<String> param = (List<String>) invocation.getArguments()[0];
                assertEquals(0, param.size());
                return null;
            }

        }).when(project).setProjectFiles(Matchers.anyListOf(String.class));

        final String filePath = "testCourse/testExercise1";
        when(projectDAO.getProjectByFile(filePath)).thenReturn(project);
        handler.handleDeletion(filePath);
        verify(project, times(1)).setStatus(ProjectStatus.DELETED);
    }

    @Test
    public void projectStatusIsSetToDownloadedIfOnDisk() {

        when(project.existsOnDisk()).thenReturn(true);
        final SnapshotInfo snapshot = new SnapshotInfo(PROJECT, "", "", "", VALID_NEW_FILE, ChangeType.FILE_CREATE);
        handler.handleSnapshot(snapshot);

        verify(project, times(1)).setStatus(ProjectStatus.DOWNLOADED);

    }

    @Test
    public void projectStatusIsNotSetToDownloadedIfNotOnDisk() {

        when(project.existsOnDisk()).thenReturn(false);

        final SnapshotInfo snapshot = new SnapshotInfo(PROJECT, "", "", "", VALID_NEW_FILE, ChangeType.FILE_CREATE);
        handler.handleSnapshot(snapshot);

        verify(project, times(1)).setStatus(ProjectStatus.NOT_DOWNLOADED);

    }

    @Test
    public void fileIsAddedToProjectWhenValidAdd() {

        final SnapshotInfo snapshot = new SnapshotInfo(PROJECT, "", "", "", VALID_NEW_FILE, ChangeType.FILE_CREATE);

        handler.handleSnapshot(snapshot);

        verifyProjectTypeInteractionWhenFileNotInProject(VALID_NEW_FILE);
        verify(project, times(1)).addProjectFile(VALID_NEW_FILE);
    }

    @Test
    public void fileIsNotAddedWhenProjectIsNotInDAO() {

        final SnapshotInfo snapshot = new SnapshotInfo("project2", "", "", "", INVALID_NEW_FILE, ChangeType.FILE_CREATE);

        handler.handleSnapshot(snapshot);

        verifyProjectTypeInteractionWhenFileNotInProject(INVALID_NEW_FILE);
        verifyNoMoreInteractions(project);
    }

    @Test
    public void folderIsAddedToProjectWhenValidAdd() {

        final SnapshotInfo snapshot = new SnapshotInfo(PROJECT, "", "", "", VALID_NEW_FOLDER, ChangeType.FOLDER_CREATE);

        handler.handleSnapshot(snapshot);

        verifyProjectTypeInteractionWhenFileNotInProject(VALID_NEW_FOLDER);
        verify(project, times(1)).addProjectFile(VALID_NEW_FOLDER);
    }

    @Test
    public void folderIsNotAddedWhenProjectIsNotInDAO() {

        final SnapshotInfo snapshot = new SnapshotInfo("project2", "", "", "", INVALID_NEW_FOLDER, ChangeType.FOLDER_CREATE);

        handler.handleSnapshot(snapshot);

        verifyProjectTypeInteractionWhenFileNotInProject(INVALID_NEW_FOLDER);
        verifyNoMoreInteractions(project);
    }

    @Test
    public void fileIsDeletedWhenIsPartOfProject() {

        final SnapshotInfo snapshot = new SnapshotInfo(PROJECT, "", "", "", EXISTING_FILE, ChangeType.FILE_DELETE);
        handler.handleSnapshot(snapshot);

        verifyProjectTypeInteractionWhenFileInProject(EXISTING_FILE);
        verify(project, times(1)).removeProjectFile(EXISTING_FILE);
    }

    @Test
    public void fileIsNotDeletedWhenProjectNotInDAO() {

        final SnapshotInfo snapshot = new SnapshotInfo("project2", "", "", "", INVALID_NEW_FILE, ChangeType.FILE_DELETE);
        handler.handleSnapshot(snapshot);
        verifyProjectTypeInteractionWhenFileNotInProject(INVALID_NEW_FILE);
        verifyNoMoreInteractions(project);
    }

    @Test
    public void folderAndFilesInFolderAreDeletedWhenIsPartOfProject() {

        final SnapshotInfo snapshot = new SnapshotInfo(PROJECT, "", "", "", EXISTING_FOLDER, ChangeType.FOLDER_DELETE);
        handler.handleSnapshot(snapshot);

        verifyProjectTypeInteractionWhenFileInProject(EXISTING_FOLDER);

        verify(project, times(1)).removeProjectFile(EXISTING_FOLDER);
        verify(project, times(1)).removeProjectFile(EXISTING_FILE);
    }

    @Test
    public void folderIsNotDeletedWhenProjectNotInDAO() {

        final SnapshotInfo snapshot = new SnapshotInfo("project2", "", "", "", INVALID_NEW_FOLDER, ChangeType.FOLDER_DELETE);
        handler.handleSnapshot(snapshot);
        verifyProjectTypeInteractionWhenFileNotInProject(INVALID_NEW_FOLDER);
        verifyNoMoreInteractions(project);
    }

    @Test
    public void fileIsRenamedWhenIsPartOfProject() {

        final SnapshotInfo snapshot = new SnapshotInfo(PROJECT, "", "", EXISTING_FILE, VALID_NEW_FILE, ChangeType.FILE_RENAME);
        handler.handleSnapshot(snapshot);
        verifyProjectTypeInteractionWhenFileInProject(EXISTING_FILE);
        verify(project, times(1)).removeProjectFile(EXISTING_FILE);
        verify(project, times(1)).addProjectFile(VALID_NEW_FILE);
    }

    @Test
    public void fileIsNotRenamedWhenProjectNotInDAO() {

        final SnapshotInfo snapshot = new SnapshotInfo("project2", "", "", INVALID_NEW_FILE, INVALID_NEW_FILE + "bar", ChangeType.FILE_RENAME);
        handler.handleSnapshot(snapshot);
        verifyProjectTypeInteractionWhenFileNotInProject(INVALID_NEW_FILE);
        verifyNoMoreInteractions(project);
    }

    @Test
    public void folderAndFilesInFolderIsRenamedWhenIsPartOfProject() {

        final SnapshotInfo snapshot = new SnapshotInfo(PROJECT, "", "", EXISTING_FOLDER, RENAMED_FOLDER, ChangeType.FOLDER_RENAME);
        handler.handleSnapshot(snapshot);
        verifyProjectTypeInteractionWhenFileInProject(EXISTING_FOLDER);
        verify(project, times(1)).removeProjectFile(EXISTING_FOLDER);
        verify(project, times(1)).addProjectFile(RENAMED_FOLDER);

        verify(project, times(1)).removeProjectFile(EXISTING_FILE);
        verify(project, times(1)).addProjectFile(RENAMED_EXISTING_FILE);

    }

    @Test
    public void noRenamesWhenFolderNotPartOfAnyProject() {

        final SnapshotInfo snapshot = new SnapshotInfo(PROJECT, "", "", "foo/bar", "foo/baz", ChangeType.FOLDER_RENAME);
        handler.handleSnapshot(snapshot);
        verifyProjectTypeInteractionWhenFileNotInProject("foo/bar");
        verifyNoMoreInteractions(project);

    }

    private void verifyProjectTypeInteractionWhenFileNotInProject(final String path) {

        verify(projectDAO, times(1)).getProjectByFile(path);
        verify(projectDAO, times(1)).getProjects();
        verify(project, times(2)).getRootPath();
    }

    private void verifyProjectTypeInteractionWhenFileInProject(final String file) {

        verify(projectDAO, times(1)).getProjectByFile(file);
        verifyNoMoreInteractions(projectDAO);
    }

}
