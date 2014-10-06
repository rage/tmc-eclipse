package fi.helsinki.cs.tmc.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class FakeFileIO extends FileIO {

    private final String path;
    private boolean fileExists;
    private boolean directoryExists;

    private byte[] byteContents;

    public FakeFileIO() {

        this("");
    }

    public FakeFileIO(final String path) {

        super("");

        this.path = path;
        fileExists = true;
        directoryExists = true;

        byteContents = new byte[0];
    }

    public void setFileExists() {

        fileExists = true;
        directoryExists = false;
    }

    public void setDirectoryExists() {

        directoryExists = true;
        fileExists = false;
    }

    public void setDoesNotExist() {

        directoryExists = false;
        fileExists = false;
    }

    public void setContents(final byte[] bytes) {

        byteContents = bytes;
    }

    public void setContents(final String contents) {

        try {
            byteContents = contents.getBytes("UTF-8");
        } catch (final UnsupportedEncodingException e) {
        }
    }

    @Override
    public String getName() {

        final String[] pathTree = path.split("/");
        return pathTree[pathTree.length - 1];
    }

    @Override
    public String getPath() {

        return path;
    }

    @Override
    public boolean fileExists() {

        return fileExists;
    }

    @Override
    public boolean directoryExists() {

        return directoryExists;
    }

    @Override
    public OutputStream getOutputStream() {

        final ByteArrayOutputStream inMemoryOutputStream = new ByteArrayOutputStream() {

            @Override
            public void flush() {

                byteContents = this.toByteArray();
            }

        };

        return inMemoryOutputStream;
    }

    @Override
    public InputStream getInputStream() {

        return new ByteArrayInputStream(byteContents);
    }

    @Override
    public Writer getWriter() {

        try {
            final OutputStream os = getOutputStream();
            if (os == null) {
                return null;
            }

            return new OutputStreamWriter(new BufferedOutputStream(os), "UTF-8");
        } catch (final IOException e) {
            return null;
        }
    }

    @Override
    public Reader getReader() {

        try {
            final InputStream is = getInputStream();
            if (is == null) {
                return null;
            }
            return new InputStreamReader(new BufferedInputStream(is), "UTF-8");
        } catch (final IOException e) {
            return null;
        }
    }

    @Override
    public byte[] read() {

        return byteContents;
    }

    @Override
    public void write(final byte[] bytes) {

        byteContents = bytes;
    }

    @Override
    public void createFolderTree(final boolean onlyParents) {

    }

    @Override
    public List<FileIO> getChildren() {

        return new ArrayList<FileIO>();
    }

}
