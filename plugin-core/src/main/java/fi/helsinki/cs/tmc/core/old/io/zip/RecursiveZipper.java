package fi.helsinki.cs.tmc.core.old.io.zip;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import fi.helsinki.cs.tmc.core.old.io.FileIO;
import fi.helsinki.cs.tmc.core.old.io.FileUtil;
import fi.helsinki.cs.tmc.core.old.io.zip.zippingdecider.ZippingDecider;

public class RecursiveZipper {

    private final FileIO rootDirectory;
    private final ZippingDecider zippingDecider;

    public RecursiveZipper(final FileIO rootDirectory, final ZippingDecider zippingDecider) {

        this.rootDirectory = rootDirectory;
        this.zippingDecider = zippingDecider;
    }

    /**
     * Zip up a project directory, only including stuff decided by the
     * {@link ZippingDecider}.
     *
     * @throws IOException
     */
    public byte[] zipProjectSources() throws IOException {

        if (!rootDirectory.directoryExists()) {
            throw new FileNotFoundException("Root directory " + rootDirectory.getPath() + " not found for zipping!");
        }

        final ByteArrayOutputStream zipBuffer = new ByteArrayOutputStream();
        final ZipOutputStream zipStream = new ZipOutputStream(zipBuffer);

        try {
            zipRecursively(rootDirectory, zipStream, "");
        } finally {
            zipStream.close();
        }

        return zipBuffer.toByteArray();
    }

    private void writeEntry(final FileIO file, final ZipOutputStream zipStream, final String zipPath) throws IOException {

        zipStream.putNextEntry(new ZipEntry(FileUtil.append(zipPath, file.getName())));

        final InputStream in = file.getInputStream();
        try {
            IOUtils.copy(in, zipStream);
        } finally {
            in.close();

        }

        zipStream.closeEntry();
    }

    /**
     * Zips a directory recursively.
     */
    private void zipRecursively(final FileIO directory, final ZipOutputStream zipStream, final String parentZipPath) throws IOException {

        String thisDirZipPath;
        if (parentZipPath.isEmpty()) {
            thisDirZipPath = directory.getName();
        } else {
            thisDirZipPath = FileUtil.append(parentZipPath, directory.getName());
        }

        // Create an entry for the directory
        zipStream.putNextEntry(new ZipEntry(FileUtil.append(thisDirZipPath, "")));
        zipStream.closeEntry();

        for (final FileIO file : directory.getChildren()) {
            try {
                final boolean isDirectory = file.directoryExists();

                String zipPath = FileUtil.append(thisDirZipPath, file.getName());
                if (isDirectory) {
                    zipPath += "/";
                }
                if (zippingDecider.shouldZip(zipPath)) {
                    if (isDirectory) {
                        zipRecursively(file, zipStream, thisDirZipPath);
                    } else {
                        writeEntry(file, zipStream, thisDirZipPath);
                    }
                }
            } finally {
            }
        }
    }
}
