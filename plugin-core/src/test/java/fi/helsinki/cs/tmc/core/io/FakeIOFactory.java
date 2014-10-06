package fi.helsinki.cs.tmc.core.io;

import java.util.HashMap;
import java.util.Map;

public class FakeIOFactory implements IOFactory {

    private Map<String, FileIO> files;

    public FakeIOFactory() {

        files = new HashMap<String, FileIO>();
    }

    @Override
    public FileIO newFile(final String path) {

        if (files.containsKey(path)) {
            return files.get(path);
        }

        final FileIO file = new FakeFileIO(path);
        files.put(path, file);
        return file;
    }

    public FakeFileIO getFake(final String path) {

        return (FakeFileIO) newFile(path);
    }

}
