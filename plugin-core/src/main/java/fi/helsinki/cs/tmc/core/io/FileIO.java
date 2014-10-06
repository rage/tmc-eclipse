package fi.helsinki.cs.tmc.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for file IO. Allows us to mock file interactions in unit tests.
 */
public class FileIO {

    private final File file;

    public FileIO(final String path) {

        file = new File(path);
    }

    public String getName() {

        return file.getName();
    }

    public String getPath() {

        return FileUtil.getUnixPath(file.getAbsolutePath());
    }

    public boolean fileExists() {

        return file.exists() && !file.isDirectory();
    }

    public boolean directoryExists() {

        return file.exists() && file.isDirectory();
    }

    /**
     * Important: It is the responsibility of the caller to close this stream!
     */
    public OutputStream getOutputStream() {

        try {
            return new FileOutputStream(file);
        } catch (final IOException e) {
            return null;
        }
    }

    /**
     * Important: It is the responsibility of the caller to close this stream!
     */
    public InputStream getInputStream() {

        try {
            return new FileInputStream(file);
        } catch (final IOException e) {
            return null;
        }
    }

    /**
     * Important: It is the responsibility of the caller to close this writer!
     */
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

    /**
     * Important: It is the responsibility of the caller to close this reader!
     */
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

    public void createFolderTree(final boolean onlyParents) {

        if (onlyParents) {
            file.getParentFile().mkdirs();
        } else {
            file.mkdirs();
        }
    }

    public List<FileIO> getChildren() {

        final List<FileIO> children = new ArrayList<FileIO>();
        if (directoryExists()) {
            for (final File f : file.listFiles()) {
                children.add(new FileIO(f.getAbsolutePath()));
            }
        }
        return children;
    }

    public void write(final byte[] bytes) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            for (int i = 0; i < bytes.length; i++) {
                fos.write(bytes[i]);
            }
        } catch (final IOException e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (final IOException e) {
                }
            }
        }
    }

    public byte[] read() {

        // TODO: implement
        return null;
    }

}
