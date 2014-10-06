package fi.helsinki.cs.tmc.core.io.zip;

import fi.helsinki.cs.tmc.core.domain.ZippedProject;
import fi.helsinki.cs.tmc.core.io.FileIO;
import fi.helsinki.cs.tmc.core.io.zip.unzippingdecider.UnzipAllTheThings;
import fi.helsinki.cs.tmc.core.io.zip.unzippingdecider.UnzippingDecider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnzipperTest {

    private String path;

    @Before
    public void setUp() {

        path = "src/test/java/fi/helsinki/cs/tmc/core/io/";
    }

    @Test
    public void unzipTestZip() throws FileNotFoundException, IOException {

        File f = new File(path + "testZip.zip");
        final FileInputStream s = new FileInputStream(f);

        final byte[] b = IOUtils.toByteArray(s);
        s.close();

        final ZippedProject project = new ZippedProject();
        final Unzipper unzipper = new Unzipper(project, new UnzipAllTheThings());
        project.setBytes(b);

        unzipper.unzipTo(new FileIO(path));

        f = new File(path + "testFile.txt");
        Scanner scanner = null;
        try {
            scanner = new Scanner(f);

            assertEquals("This is a test. ", scanner.nextLine());
            assertEquals(false, scanner.hasNextLine());
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            f.delete();
        }
    }

    @Test
    public void unzippingCanCreateFolders() throws FileNotFoundException, IOException {

        unZipDirectory("testDirectory.zip");

        File f = new File(path + "testDirectory/testFile.txt");
        Scanner scanner = null;
        try {
            assertEquals(true, f.exists());
            scanner = new Scanner(f);

            for (int i = 0; i < 5000; i++) {
                assertEquals("This is a test. ", scanner.nextLine());
            }
            assertEquals(false, scanner.hasNextLine());
            f = new File(path + "testDirectory/dir");
            assertEquals(true, f.exists());
            assertEquals(true, f.isDirectory());

        } finally {
            if (scanner != null) {
                scanner.close();
            }
            FileUtils.deleteDirectory(new File(path + "testDirectory/"));
        }
    }

    @Test
    public void unzippingAFolderDoesNotOverWriteFilesInExistingFolder() throws FileNotFoundException, IOException {

        unZipDirectory("testDirectory.zip");
        unZipDirectory("overWriteTest.zip");
        try {
            File f = new File(path + "testDirectory/eclipse");
            assertEquals(true, f.exists());
            assertEquals(false, f.isDirectory());

            f = new File(path + "testDirectory/testFile.txt");
            assertEquals(true, f.exists());
            assertEquals(false, f.isDirectory());

            f = new File(path + "testDirectory/dir");
            assertEquals(true, f.exists());
            assertEquals(true, f.isDirectory());

            f = new File(path + "testDirectory");
            assertEquals(true, f.exists());
            assertEquals(true, f.isDirectory());
        } finally {
            FileUtils.deleteDirectory(new File(path + "testDirectory/"));
        }

    }

    private void assertFileOk(final String file, final String content) throws FileNotFoundException {

        final File f = new File(path + file);
        Scanner scanner = null;
        try {
            assertEquals(true, f.exists());
            assertEquals(false, f.isDirectory());
            scanner = new Scanner(f);
            assertEquals(content, scanner.nextLine());
        } finally {
            scanner.close();
        }
    }

    private void unZipDirectory(final String zip) throws IOException, FileNotFoundException {

        unZipDirectory(zip, new UnzipAllTheThings());
    }

    private void unZipDirectory(final String zip, final UnzippingDecider decider) throws IOException, FileNotFoundException {

        final File f = new File(path + zip);
        final byte[] b = IOUtils.toByteArray(new FileInputStream(f));
        final ZippedProject project = new ZippedProject();
        project.setBytes(b);
        final Unzipper unzipper = new Unzipper(project, decider);
        unzipper.unzipTo(new FileIO(path));
    }

}
