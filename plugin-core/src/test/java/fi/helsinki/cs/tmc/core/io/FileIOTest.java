package fi.helsinki.cs.tmc.core.io;

import java.io.IOException;
import java.io.Reader;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FileIOTest {

    private FileIO io;

    @Before
    public void setUp() {

        io = new FileIO("invalid.file");
    }

    @Test
    public void testGetReaderReturnsNullIfFileDoesntExist() {

        assertNull(io.getReader());
    }

    @Test
    public void testFileExistsAndGetName() {

        assertEquals(false, io.fileExists());
        assertEquals(false, new FileIO("src/").fileExists());
        assertEquals(true, new FileIO("pom.xml").fileExists());
        assertEquals("pom.xml", new FileIO("pom.xml").getName());
    }

    @Test
    public void testGetreader() throws IOException {

        final FileIO fio = new FileIO("pom.xml");
        final Reader r = fio.getReader();
        assertEquals((char) r.read(), '<');
        assertEquals((char) r.read(), 'p');
    }

}
