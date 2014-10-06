package fi.helsinki.cs.tmc.core.services;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.storage.DataSource;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProjectDAOTest {

    private ProjectDAO dao;
    private DataSource<Project> source;
    private List<Project> projectList;
    private List<String> projectFileList;

    @Before
    public void setUp() throws Exception {

        source = new TestProjectDataSource();
        projectList = new ArrayList<Project>();
        projectFileList = new ArrayList<String>();
        projectFileList.add("directory/pom.xml");

        projectList.add(new Project(new Exercise("name1", "course1"), projectFileList));
        projectList.add(new Project(new Exercise("name2", "course1"), projectFileList));
        projectList.add(new Project(new Exercise("name3", "course1"), projectFileList));
        projectList.add(new Project(new Exercise("name4", "course2"), projectFileList));
        projectList.add(new Project(new Exercise("name5", "course2"), projectFileList));
        projectList.add(new Project(new Exercise("name6", "course2"), projectFileList));
        source.save(projectList);

        dao = new ProjectDAO(source);
    }

    @Test
    public void testGetProjects() {

        assertEquals(projectList, dao.getProjects());
    }

    @Test
    public void addingProjects() {

        dao.addProject(new Project(new Exercise("name7", "course3"), projectFileList));

        assertEquals(dao.getProjectByExercise(projectList.get(projectList.size() - 1).getExercise()).getExercise().getCourseName(), "course3");

        dao.addProject(new Project(new Exercise("name2", "course1"), projectFileList));

        assertEquals(dao.getProjects().size(), 7);
    }

    @Test
    public void getProjectByExerciseReturnsNullIfExerciseNotFound() {

        assertEquals(null, dao.getProjectByExercise(new Exercise("asdasd", "asdasd")));
    }

    @Test
    public void testGetProjectByFile() {

        assertTrue(dao.getProjectByFile("asdasdasd") == null);
        assertTrue(dao.getProjectByFile("directory") != null);
    }

    public class TestProjectDataSource implements DataSource<Project> {

        List<Project> list;

        public TestProjectDataSource() {

        }

        @Override
        public List<Project> load() {

            return list;
        }

        @Override
        public void save(final List<Project> elements) {

            list = elements;
        }

    }
}
