package fi.helsinki.cs.tmc.core.spyware.async;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.spyware.DocumentInfo;
import fi.helsinki.cs.tmc.core.spyware.services.EventReceiver;
import fi.helsinki.cs.tmc.core.spyware.services.LoggableEvent;
import fi.helsinki.cs.tmc.core.spyware.utility.diff_match_patch;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
 * If you are here for find out why the test randomly freezes, it's the clipboard function in paste event test
 * */
public class DocumentSendThreadTest {

    private DocumentSendThread thread;
    private EventReceiver receiver;
    private DocumentInfo info;
    private Project project;
    private Map<String, String> cache;
    private diff_match_patch patchGenerator;

    @Before
    public void setUp() throws Exception {

        receiver = mock(EventReceiver.class);
        info = new DocumentInfo("a", "a", "a", "a", 0, 1);
        project = mock(Project.class);
        cache = new HashMap<String, String>();
        patchGenerator = mock(diff_match_patch.class);

        when(project.getExercise()).thenReturn(new Exercise("name1", "course1"));

        thread = new DocumentSendThread(receiver, info, project, cache, patchGenerator);
    }

    @Test
    public void generatePatchesTest() {

        thread.run();
        assertEquals(cache.size(), 1);
    }

    @Test
    public void insertEventTest() {

        Mockito.doAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {

                final LoggableEvent event = (LoggableEvent) invocation.getArguments()[0];
                assertEquals("text_insert", event.getEventType());
                return null;
            }
        }).when(receiver).receiveEvent(any(LoggableEvent.class));

        thread.run();

        verify(receiver, times(1)).receiveEvent(any(LoggableEvent.class));
    }

    @Test
    public void removeEventTest() {

        info = new DocumentInfo("", "", "", "", 0, 1);
        thread = new DocumentSendThread(receiver, info, project, cache, patchGenerator);

        Mockito.doAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {

                final LoggableEvent event = (LoggableEvent) invocation.getArguments()[0];
                assertEquals("text_remove", event.getEventType());
                return null;
            }
        }).when(receiver).receiveEvent(any(LoggableEvent.class));

        thread.run();
        verify(receiver, times(1)).receiveEvent(any(LoggableEvent.class));
    }

    @Test
    public void pasteEventTest() {

        // travis does not have necessary components to run this test
        if ("true".equals(System.getenv("TRAVIS"))) {
            return;
        }

        /* THIS PART OCCASIONALLY FREEZES, NO IDEA WHY */
        final StringSelection s = new StringSelection("a a");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, s);

        info = new DocumentInfo("", "", "", "a a", 0, 3);
        thread = new DocumentSendThread(receiver, info, project, cache, patchGenerator);

        Mockito.doAnswer(new Answer() {

            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {

                final LoggableEvent event = (LoggableEvent) invocation.getArguments()[0];
                assertEquals("text_paste", event.getEventType());
                return null;
            }
        }).when(receiver).receiveEvent(any(LoggableEvent.class));

        thread.run();
        verify(receiver, times(1)).receiveEvent(any(LoggableEvent.class));
    }

}
