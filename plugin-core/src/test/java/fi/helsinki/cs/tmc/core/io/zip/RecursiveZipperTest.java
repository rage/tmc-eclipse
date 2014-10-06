package fi.helsinki.cs.tmc.core.io.zip;

import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.domain.ZippedProject;
import fi.helsinki.cs.tmc.core.io.FileIO;
import fi.helsinki.cs.tmc.core.io.FileUtil;
import fi.helsinki.cs.tmc.core.io.zip.unzippingdecider.UnzipAllTheThings;
import fi.helsinki.cs.tmc.core.io.zip.zippingdecider.DefaultZippingDecider;
import fi.helsinki.cs.tmc.core.io.zip.zippingdecider.MavenZippingDecider;
import fi.helsinki.cs.tmc.core.io.zip.zippingdecider.ZipAllTheThings;
import fi.helsinki.cs.tmc.core.io.zip.zippingdecider.ZippingDecider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecursiveZipperTest {
    
    private static final String TEST_DIR = "zippingDeciderTest/";

    private String path;

    @Before
    public void setUp() {

        path = "src/test/java/fi/helsinki/cs/tmc/core/io/";
    }

    @Test
    public void testZippingDirectory() throws Exception {

        try {
            unzipFolder("testDirectory.zip");

            zipDirectory(new FileIO(FileUtil.append(path, "testDirectory")), "testDirectoryOutput.zip");

            final ZipFile original = new ZipFile(FileUtil.append(path, "testDirectory.zip"));
            final ZipFile zipped = new ZipFile(FileUtil.append(path, "testDirectoryOutput.zip"));

            final Set<Long> originalContents = new LinkedHashSet<Long>();
            for (final Enumeration<?> e = original.entries(); e.hasMoreElements();) {
                originalContents.add(((ZipEntry) e.nextElement()).getCrc());
            }

            final Set<Long> zippedContents = new LinkedHashSet<Long>();
            for (final Enumeration<?> e = zipped.entries(); e.hasMoreElements();) {
                zippedContents.add(((ZipEntry) e.nextElement()).getCrc());
            }

            original.close();
            zipped.close();

            assertEquals(originalContents, zippedContents);
        } finally {
            FileUtils.deleteDirectory(new File(FileUtil.append(path, "testDirectory/")));
        }
    }

    @Test
    public void testZippingDirectoryWithDefaultZippingDecider() throws Exception {

        try {
            prepareZippingDeciderTest("zippingdecider_default_original.zip", new DefaultZippingDecider(mockProject()));

            File f;

            f = new File(path + TEST_DIR);
            assertEquals(true, f.exists());
            assertEquals(true, f.isDirectory());

            f = new File(path + "zippingDeciderTest/Shouldnotbeincluded");
            assertEquals(false, f.exists());

            f = new File(path + "zippingDeciderTest/notzippable/");
            assertEquals(false, f.exists());

            f = new File(path + "zippingDeciderTest/src/");
            assertEquals(true, f.exists());
            assertEquals(true, f.isDirectory());

            f = new File(path + "zippingDeciderTest/src/ShouldbeIncluded");
            assertEquals(true, f.exists());
            assertEquals(false, f.isDirectory());

            f = new File(path + "zippingDeciderTest/src/ignoredFolder");
            assertEquals(false, f.exists());

        } finally {
            FileUtils.deleteDirectory(new File(path + TEST_DIR));
        }
    }

    @Test
    public void testZippingDirectoryWithMavenZippingDecider() throws Exception {

        try {
            prepareZippingDeciderTest("zippingdecider_maven_original.zip", new MavenZippingDecider(mockProject()));

            File f;

            f = new File(path + TEST_DIR);
            assertEquals(true, f.exists());
            assertEquals(true, f.isDirectory());

            f = new File(path + "zippingDeciderTest/target");
            assertEquals(false, f.exists());

            f = new File(path + "zippingDeciderTest/lib");
            assertEquals(true, f.exists());
            assertEquals(true, f.isDirectory());

            f = new File(path + "zippingDeciderTest/lib/should_be_included");
            assertEquals(true, f.exists());
            assertEquals(false, f.isDirectory());

            f = new File(path + "zippingDeciderTest/lib/testrunner");
            assertEquals(false, f.exists());

            f = new File(path + "zippingDeciderTest/src/");
            assertEquals(true, f.exists());
            assertEquals(true, f.isDirectory());

            f = new File(path + "zippingDeciderTest/src/should_be_zipped");
            assertEquals(true, f.exists());
            assertEquals(false, f.isDirectory());

            f = new File(path + "zippingDeciderTest/src/shouldbezipped");
            assertEquals(true, f.exists());
            assertEquals(true, f.isDirectory());

            f = new File(path + "zippingDeciderTest/src/shouldbezipped/should_be_zipped");
            assertEquals(true, f.exists());
            assertEquals(false, f.isDirectory());

            f = new File(path + "zippingDeciderTest/src/shouldbeignored");
            assertEquals(false, f.exists());

        } finally {
            FileUtils.deleteDirectory(new File(path + TEST_DIR));
        }
    }

    private Project mockProject() {

        final Project project = mock(Project.class);
        when(project.getExtraStudentFiles()).thenReturn(new ArrayList<String>());
        when(project.getRootPath()).thenReturn(path + TEST_DIR);
        return project;
    }

    private void prepareZippingDeciderTest(final String zipname, final ZippingDecider decider) throws IOException, FileNotFoundException, Exception {

        try {
            // unzip test files
            unzipFolder(zipname);
            // zip test files with zippingdecider that ignores some of them
            zipDirectory(new FileIO(path + "zippingDeciderTest"), "zippingDeciderTest.zip", decider);
            // erase the directory
            FileUtils.deleteDirectory(new File(path + TEST_DIR));

            unzipFolder("zippingDeciderTest.zip");
        } finally {
            (new File(path + "zippingDeciderTest.zip")).delete();
        }
    }

    private void unzipFolder(final String zipname) throws IOException, FileNotFoundException {

        final File f = new File(FileUtil.append(path, zipname));

        final byte[] b = IOUtils.toByteArray(new FileInputStream(f));
        final ZippedProject project = new ZippedProject();
        project.setBytes(b);
        final Unzipper unzipper = new Unzipper(project, new UnzipAllTheThings());
        unzipper.unzipTo(new FileIO(path));
    }

    private void zipDirectory(final FileIO directory, final String zipName) throws Exception {

        zipDirectory(directory, zipName, new ZipAllTheThings());
    }

    private void zipDirectory(final FileIO directory, final String zipName, final ZippingDecider decider) throws Exception {

        final RecursiveZipper zipper = new RecursiveZipper(directory, decider);

        final byte[] zip = zipper.zipProjectSources();

        final OutputStream os = new FileIO(FileUtil.append(path, zipName)).getOutputStream();
        try {
            IOUtils.write(zip, os);
        } finally {
            os.close();
        }
    }

}
