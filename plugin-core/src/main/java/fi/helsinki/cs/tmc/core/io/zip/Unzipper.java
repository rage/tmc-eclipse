package fi.helsinki.cs.tmc.core.io.zip;

import fi.helsinki.cs.tmc.core.domain.ZippedProject;
import fi.helsinki.cs.tmc.core.io.FileIO;
import fi.helsinki.cs.tmc.core.io.FileUtil;
import fi.helsinki.cs.tmc.core.io.zip.unzippingdecider.UnzippingDecider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzipper {

    private static final int BUFFER_SIZE = 1024;

    private final ZippedProject project;
    private final UnzippingDecider decider;

    public Unzipper(final ZippedProject project, final UnzippingDecider decider) {

        this.project = project;
        this.decider = decider;
    }

    public List<String> unzipTo(final FileIO destinationFolder) throws IOException {

        final List<String> projectFiles = new ArrayList<String>();

        destinationFolder.createFolderTree(false);
        final ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(project.getBytes()));
        ZipEntry zipEntry = zipStream.getNextEntry();

        while (zipEntry != null) {
            final String entryPath = FileUtil.append(destinationFolder.getPath(), zipEntry.getName());

            projectFiles.add(entryPath);

            if (!decider.shouldUnzip(entryPath)) {
                zipEntry = zipStream.getNextEntry();
                continue;
            }

            final FileIO file = new FileIO(entryPath);
            file.createFolderTree(!zipEntry.isDirectory());

            byte[] buffer = new byte[BUFFER_SIZE];
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            int read = 0;
            while ((read = zipStream.read(buffer)) != -1) {
                stream.write(buffer, 0, read);
                buffer = new byte[BUFFER_SIZE];
            }

            if (!zipEntry.isDirectory()) {
                file.write(stream.toByteArray());
            }

            zipEntry = zipStream.getNextEntry();
        }

        return projectFiles;
    }

}
