package fi.helsinki.cs.plugin.tmc.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.helsinki.cs.plugin.tmc.io.FileUtil;
import fi.helsinki.cs.plugin.tmc.io.zipper.zippingdecider.DefaultZippingDecider;
import fi.helsinki.cs.plugin.tmc.io.zipper.zippingdecider.MavenZippingDecider;
import fi.helsinki.cs.plugin.tmc.io.zipper.zippingdecider.ZippingDecider;

public class Project {

    private Exercise exercise;
    private List<String> projectFiles;
    private List<String> extraStudentFiles;
    private String rootPath;
    private ProjectStatus status;

    public Project(Exercise exercise) {
        this(exercise, new ArrayList<String>());
        this.status = ProjectStatus.NOT_DOWNLOADED;

        exercise.setProject(this);
    }

    public Project(Exercise exercise, List<String> projectFiles) {
        this.exercise = exercise;
        this.projectFiles = projectFiles;
        this.extraStudentFiles = Collections.emptyList();
        this.rootPath = buildRootPath();
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public String getRootPath() {
        return rootPath;
    }

    public ProjectType getProjectType() {
        return ProjectType.findProjectType(projectFiles);
    }

    public boolean containsFile(String file) {
        if (file == null) {
            return false;
        }
        return file.contains(rootPath);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Project)) {
            return false;
        }

        Project p = (Project) o;
        if (this.exercise == null || p.exercise == null) {
            return false;
        }

        return this.exercise.equals(p.exercise);
    }

    @Override
    public int hashCode() {
        if (exercise == null) {
            return 0;
        }

        return exercise.hashCode();
    }

    private String buildRootPath() {
        ProjectType type = getProjectType();
        if (type == null) {
            return "";
        }

        for (String file : projectFiles) {
            if (file.contains(type.getBuildFile())) {
                return FileUtil.getUnixPath(file.replace(type.getBuildFile(), ""));
            }
        }
        return "";
    }

    public ZippingDecider getZippingDecider() {
        switch (getProjectType()) {
        case JAVA_MAVEN:
            return new MavenZippingDecider(this);
        case JAVA_ANT:
            return new DefaultZippingDecider(this);
        case MAKEFILE:
            return new DefaultZippingDecider(this);
        default:
            throw new RuntimeException("Invalid project type");
        }
    }

    public void setProjectFiles(List<String> files) {
        projectFiles = files;
    }

    public List<String> getProjectFiles() {
        return projectFiles;
    }

    public void setExtraStudentFiles(List<String> files) {
        extraStudentFiles = files;
    }

    public List<String> getExtraStudentFiles() {
        return extraStudentFiles;
    }

    public boolean existsOnDisk() {
        for (String file : projectFiles) {
            if (file.replace(getRootPath(), "").contains("/src/")) {
                return true;
            }
        }
        return false;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

}
