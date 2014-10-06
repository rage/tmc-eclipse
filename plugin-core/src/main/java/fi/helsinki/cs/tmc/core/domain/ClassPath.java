package fi.helsinki.cs.tmc.core.domain;

import fi.helsinki.cs.tmc.core.io.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClassPath {

    private List<String> subPaths = new ArrayList<String>();

    public ClassPath(final String classpath) {

        subPaths.add(FileUtil.getUnixPath(classpath));
    }

    public void add(final String classpath) {

        if (!subPaths.contains(FileUtil.getUnixPath(classpath))) {
            subPaths.add(FileUtil.getUnixPath(classpath));
        }
    }

    public void add(final ClassPath classpath) {

        for (final String cp : classpath.subPaths) {
            add(FileUtil.getUnixPath(cp));
        }
    }

    public void setSubPaths(final List<String> subPaths) {

        this.subPaths = subPaths;
    }

    public List<String> getSubPaths() {

        return subPaths;
    }

    @Override
    public String toString() {

        String cp = subPaths.get(0);

        for (int i = 1; i < subPaths.size(); i++) {
            cp += System.getProperty("path.separator") + subPaths.get(i);
        }

        return cp;
    }

    public void addDirAndSubDirs(final String path) {
        
        File root = new File(path);
        
        String rootPath = path;
        if (!root.isDirectory()) {
            rootPath = FileUtil.getUnixPath(root.getParent());
            root = root.getParentFile();
        }

        if (rootPath.endsWith("/")) {
            rootPath = rootPath.substring(0, path.length() - 1);
        }
        final String classpath = rootPath + "/*";

        if (!subPaths.contains(FileUtil.getUnixPath(classpath))) {
            subPaths.add(FileUtil.getUnixPath(classpath));
            for (final File child : root.listFiles()) {
                if (child.isDirectory()) {
                    addDirAndSubDirs(FileUtil.getUnixPath(child.getAbsolutePath()));
                }
            }
        }
    }
}
