package fi.helsinki.cs.tmc.core.services.http;

import com.google.gson.Gson;

import fi.helsinki.cs.tmc.core.domain.Course;
import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.FeedbackAnswer;
import fi.helsinki.cs.tmc.core.domain.FeedbackQuestion;
import fi.helsinki.cs.tmc.core.domain.Review;
import fi.helsinki.cs.tmc.core.domain.ZippedProject;
import fi.helsinki.cs.tmc.core.services.Settings;
import fi.helsinki.cs.tmc.core.spyware.services.LoggableEvent;
import fi.helsinki.cs.tmc.core.ui.ObsoleteClientException;
import fi.helsinki.cs.tmc.core.ui.UserVisibleException;
import fi.helsinki.cs.tmc.core.utils.jsonhelpers.CourseList;
import fi.helsinki.cs.tmc.core.utils.jsonhelpers.ExerciseList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServerManagerTest {
    
    private static final String URL = "url";
    private static final String API_URL = "apiUrl";

    private ConnectionBuilder connectionBuilder;
    private Gson gson;
    private ServerManager server;
    private Settings settings;
    private RequestBuilder rb;

    @Before
    public void setUp() {

        connectionBuilder = mock(ConnectionBuilder.class);
        gson = new Gson();
        settings = mock(Settings.class);
        server = new ServerManager(gson, connectionBuilder, settings);
        rb = mock(RequestBuilder.class);
        when(connectionBuilder.createConnection()).thenReturn(rb);
    }

    @Test
    public void getCoursesReturnsValidCoursesOnSuccefullHttpRequest() throws Exception {

        final CourseList cl = buildMockCourseList();
        final String mockJson = gson.toJson(cl);

        when(rb.getForText(any(String.class))).thenReturn(mockJson);

        final List<Course> returnedCourses = server.getCourses();

        for (int i = 1; i < cl.getCourses().length; i++) {
            final Course c = cl.getCourses()[i];
            boolean found = false;
            for (final Course returned : returnedCourses) {
                if (returned.getName().equals(c.getName())) {
                    found = true;
                }
            }
            assertTrue("Didn't find all courses that should have been present, based on the JSON", found);
        }

        assertEquals(cl.getCourses().length, returnedCourses.size());
        assertEquals("7", cl.getApiVersion());
    }

    @Test
    public void getcoursesReturnEmptyListOnFailure() throws Exception {

        when(rb.getForText(any(String.class))).thenReturn("");

        assertEquals(0, server.getCourses().size());
    }

    @Test
    public void getExercisesReturnsValidObjectOnSuccesfullHttpRequest() throws Exception {

        final ExerciseList el = buildMockExerciseList();
        final String mockJson = gson.toJson(el);

        when(rb.getForText(any(String.class))).thenReturn(mockJson);

        final List<Exercise> returned = server.getExercises("11");

        for (final Exercise orig : el.getExercises()) {
            boolean found = false;
            for (final Exercise ret : returned) {
                if (ret.getName().equals(orig.getName())) {
                    found = true;
                }
            }
            assertTrue("The returned list of exercises should contain all exercises of the original list", found);
        }
    }

    @Test
    public void getExerciseZipShouldReturnTheByteArrayGottenFromTheHttpRequest() throws Exception {

        final byte[] byteArray = { 1, 0, 1 };
        when(rb.getForBinary(any(String.class))).thenReturn(byteArray);

        final ZippedProject returned = server.getExerciseZip(URL);
        assertEquals(byteArray, returned.getBytes());
    }

    @Test
    public void getExerciseZipShouldReturnEmptyByteArrayOnFailedRequest() throws Exception {

        when(rb.getForBinary(any(String.class))).thenThrow(new IOException());

        assertEquals(0, server.getExerciseZip(URL).getBytes().length);
    }

    @Test
    public void submitFeedbackCreatesConnectionAndCallsPostForText() throws Exception {

        when(connectionBuilder.addApiCallQueryParameters(Matchers.anyString())).thenReturn(API_URL);

        server.submitFeedback(URL, new ArrayList<FeedbackAnswer>());
        verify(connectionBuilder, times(1)).createConnection();
        verify(connectionBuilder, times(1)).addApiCallQueryParameters(URL);
        verify(rb, times(1)).postForText(eq(API_URL), anyMap());
    }

    @Test
    public void submitFeedbackPostForTextMapHasCorrectValues() throws Exception {

        final List<FeedbackAnswer> answers = new ArrayList<FeedbackAnswer>();
        answers.add(new FeedbackAnswer(new FeedbackQuestion(0, "Question", "text"), "Answer"));

        final String apiUrl = "";
        when(connectionBuilder.addApiCallQueryParameters(Matchers.anyString())).thenReturn(apiUrl);

        when(rb.postForText(Matchers.anyString(), anyMap())).thenAnswer(new Answer() {

            @Override
            public String answer(final InvocationOnMock invocation) {

                final Object[] args = invocation.getArguments();

                final Map<String, String> paramMap = (HashMap<String, String>) args[1];

                assertEquals(2, paramMap.keySet().size());
                assertEquals(2, paramMap.values().size());

                assertTrue(paramMap.keySet().contains("answers[0][question_id]"));
                assertTrue(paramMap.keySet().contains("answers[0][answer]"));

                assertEquals("0", paramMap.get("answers[0][question_id]"));
                assertEquals("Answer", paramMap.get("answers[0][answer]"));

                return "";
            }
        });

        server.submitFeedback(URL, answers);
    }

    @Test(expected = RuntimeException.class)
    public void submitFeedbackThrowsRuntimeExceptionOnNonHttpException() throws Exception {

        when(rb.postForText(Matchers.anyString(), anyMap())).thenThrow(new Exception("Derp"));
        server.submitFeedback(URL, new ArrayList<FeedbackAnswer>());
    }

    @Test(expected = ObsoleteClientException.class)
    public void submitFeedbackThrowsObsoleteClientExceptionWhenStatusCodeIs404AndIsObsolete() throws Exception {

        final FailedHttpResponseException exception = mock(FailedHttpResponseException.class);
        when(exception.getStatusCode()).thenReturn(404);
        when(exception.getEntityAsString()).thenReturn("{obsolete_client:true}");

        when(rb.postForText(Matchers.anyString(), anyMap())).thenThrow(exception);
        server.submitFeedback(URL, new ArrayList<FeedbackAnswer>());
    }

    @Test(expected = UserVisibleException.class)
    public void submitFeedbackThrowsHttpExceptionWhenStatusCodeIsNot404() throws Exception {

        final FailedHttpResponseException exception = mock(FailedHttpResponseException.class);
        when(exception.getStatusCode()).thenReturn(403);

        when(rb.postForText(Matchers.anyString(), anyMap())).thenThrow(exception);
        server.submitFeedback(URL, new ArrayList<FeedbackAnswer>());
    }

    @Test(expected = UserVisibleException.class)
    public void submitFeedbackThrowsHttpExceptionWhenStatusCodeIs404ButIsNotObsolete() throws Exception {

        final FailedHttpResponseException exception = mock(FailedHttpResponseException.class);
        when(exception.getStatusCode()).thenReturn(404);
        when(exception.getEntityAsString()).thenReturn("{obsolete_client:false}");

        when(rb.postForText(Matchers.anyString(), anyMap())).thenThrow(exception);
        server.submitFeedback(URL, new ArrayList<FeedbackAnswer>());
    }

    @Test
    public void uploadFilesCreatesConnectionAndCallsUploadFileForTextDownload() throws Exception {

        final Exercise exercise = mock(Exercise.class);
        when(exercise.getReturnUrl()).thenReturn(URL);

        when(connectionBuilder.addApiCallQueryParameters(URL)).thenReturn(API_URL);
        when(settings.getErrorMsgLocale()).thenReturn(Locale.ENGLISH);
        final byte[] data = new byte[5];

        try {
            server.uploadFile(exercise, data);
        } catch (final Exception e) {
            // method throws due to malformed json; it's ok as it happens after
            // the method calls this test is interested in
        }

        verify(connectionBuilder, times(1)).createConnection();
        verify(connectionBuilder, times(1)).addApiCallQueryParameters(URL);
        verify(rb, times(1)).uploadFileForTextDownload(eq(API_URL), anyMap(), eq("submission[file]"), eq(data));

    }

    @Test(expected = RuntimeException.class)
    public void uploadFilesThrowsIfJsonContainsErrorField() throws Exception {

        final byte[] data = new byte[5];

        final Exercise exercise = mock(Exercise.class);
        when(exercise.getReturnUrl()).thenReturn(URL);

        when(connectionBuilder.addApiCallQueryParameters(URL)).thenReturn(API_URL);
        when(rb.uploadFileForTextDownload(eq(API_URL), anyMap(), eq("submission[file]"), eq(data))).thenReturn("{error:error_message}");

        server.uploadFile(exercise, data);
    }

    @Test(expected = RuntimeException.class)
    public void uploadFilesThrowsIfJDoesNotContainErrorOrSubmissionUrlFields() throws Exception {

        final byte[] data = new byte[5];

        final Exercise exercise = mock(Exercise.class);
        when(exercise.getReturnUrl()).thenReturn(URL);

        when(connectionBuilder.addApiCallQueryParameters(URL)).thenReturn(API_URL);
        when(rb.uploadFileForTextDownload(eq(API_URL), anyMap(), eq("submission[file]"), eq(data))).thenReturn("{foo:bar}");

        server.uploadFile(exercise, data);
    }

    @Test(expected = RuntimeException.class)
    public void uploadFilesThrowsIfUrlIsMalformed() throws Exception {

        final byte[] data = new byte[5];

        final Exercise exercise = mock(Exercise.class);
        when(exercise.getReturnUrl()).thenReturn(URL);

        when(connectionBuilder.addApiCallQueryParameters(URL)).thenReturn(API_URL);
        when(rb.uploadFileForTextDownload(eq(API_URL), anyMap(), eq("submission[file]"), eq(data))).thenReturn(
                "{submission_url:\"http:/www.asdsads%ad.com.\", paste_url:\"htp:///ww.ab%c\\//.co./\"}");

        server.uploadFile(exercise, data);
    }

    @Test
    public void uploadFilesReturnsCorrectResponse() throws Exception {

        final byte[] data = new byte[5];

        final Exercise exercise = mock(Exercise.class);
        when(exercise.getReturnUrl()).thenReturn(URL);

        when(connectionBuilder.addApiCallQueryParameters(URL)).thenReturn(API_URL);
        when(rb.uploadFileForTextDownload(eq(API_URL), anyMap(), eq("submission[file]"), eq(data))).thenReturn(
                "{submission_url:\"http://www.submission_url.com\", paste_url:\"http://www.paste_url.com\"}");

        when(settings.getErrorMsgLocale()).thenReturn(Locale.ENGLISH);
        final SubmissionResponse r = server.uploadFile(exercise, data);
        assertEquals(r.getSubmissionUrl().toString(), "http://www.submission_url.com");
        assertEquals(r.getPasteUrl().toString(), "http://www.paste_url.com");
    }

    private CourseList buildMockCourseList() {

        final Course c1 = new Course("c1");
        final Course c2 = new Course("c2");
        final Course c3 = new Course("c3");
        final Course[] cl = { c1, c2, c3 };
        final CourseList courseList = new CourseList();
        courseList.setCourses(cl);
        courseList.setApiVersion("7");
        return courseList;
    }

    private ExerciseList buildMockExerciseList() {

        final Exercise e1 = new Exercise("e1");
        final Exercise e2 = new Exercise("e2");
        final Exercise e3 = new Exercise("e3");
        final List<Exercise> exercises = new ArrayList<Exercise>();
        exercises.add(e1);
        exercises.add(e2);
        exercises.add(e3);

        final Course course = new Course("c1");
        course.setExercises(exercises);

        final ExerciseList eList = new ExerciseList();
        eList.setCourse(course);
        return eList;
    }

    @Test
    public void sendEventLogCallsAddApiCallQueryParameters() {

        server.sendEventLogs(URL, new ArrayList<LoggableEvent>());
        verify(connectionBuilder, times(1)).addApiCallQueryParameters(URL);
    }

    public void sendEventLogCallsSettingsCorrectly() {

        server.sendEventLogs(URL, new ArrayList<LoggableEvent>());
        verify(settings, times(1)).getUsername();
        verify(settings, times(1)).getPassword();

    }

    @Test
    public void sendEventLogSetsExtraHeadersCorrectly() throws Exception {

        when(rb.rawPostForText(Matchers.anyString(), Matchers.any(byte[].class), anyMap())).thenAnswer(new Answer() {

            @Override
            public String answer(final InvocationOnMock invocation) {

                final Object[] args = invocation.getArguments();

                final Map<String, String> paramMap = (HashMap<String, String>) args[2];

                assertEquals(3, paramMap.keySet().size());
                assertEquals(3, paramMap.values().size());

                assertTrue(paramMap.keySet().contains("X-Tmc-Version"));
                assertTrue(paramMap.keySet().contains("X-Tmc-Username"));
                assertTrue(paramMap.keySet().contains("X-Tmc-Password"));

                assertEquals("1", paramMap.get("X-Tmc-Version"));
                assertEquals("username", paramMap.get("X-Tmc-Username"));
                assertEquals("password", paramMap.get("X-Tmc-Password"));

                return "";
            }
        });

    }

    @Test(expected = RuntimeException.class)
    public void sendEventLogsThrowsRuntimeExceptionWhenPostForTextThrows() throws Exception {

        when(rb.rawPostForText(Matchers.anyString(), Matchers.any(byte[].class), anyMap())).thenThrow(new Exception("Error"));

        server.sendEventLogs(URL, new ArrayList<LoggableEvent>());
    }

    @Test
    public void markReviewAsReadGetsApiParameters() {

        final Review review = mock(Review.class);
        when(review.getUpdateUrl()).thenReturn(URL);
        server.markReviewAsRead(review);
        verify(connectionBuilder, times(1)).addApiCallQueryParameters(URL + ".json");
    }

    @Test
    public void markReviewAsReadCreatesConnection() {

        final Review review = mock(Review.class);
        when(review.getUpdateUrl()).thenReturn(URL);
        server.markReviewAsRead(review);
        verify(connectionBuilder, times(1)).createConnection();
    }

    @Test
    public void markReviewAsReadCatchesExceptionsIfPostForTextThrows() throws Exception {

        final Review review = mock(Review.class);
        when(review.getUpdateUrl()).thenReturn(URL);
        when(rb.postForText(anyString(), anyMap())).thenThrow(new RuntimeException("Foo"));
        server.markReviewAsRead(review);
        verify(rb, times(1)).postForText(anyString(), anyMap());
    }

    @Test
    public void markReviewAsReadCallsPostForText() throws Exception {

        final Review review = mock(Review.class);
        when(review.getUpdateUrl()).thenReturn(URL);
        server.markReviewAsRead(review);
        verify(rb, times(1)).postForText(anyString(), anyMap());
    }

    @Test
    public void downloadReviewsAddsApiParameters() {

        final Course course = mock(Course.class);
        when(course.getReviewsUrl()).thenReturn(URL);
        server.downloadReviews(course);
        verify(connectionBuilder, times(1)).addApiCallQueryParameters(URL);
    }

    @Test
    public void downloadReviewsReadCreatesConnection() {

        final Course course = mock(Course.class);
        when(course.getReviewsUrl()).thenReturn(URL);
        server.downloadReviews(course);
        verify(connectionBuilder, times(1)).createConnection();
    }

    @Test
    public void downloadReviewsReadCallsGetForText() throws Exception {

        final Course course = mock(Course.class);
        when(course.getReviewsUrl()).thenReturn(URL);
        server.downloadReviews(course);
        verify(rb, times(1)).getForText(anyString());
    }

    @Test
    public void downloadReviewsDoesCatchesExceptionIfGetForTextThrows() throws Exception {

        final Course course = mock(Course.class);
        when(course.getReviewsUrl()).thenReturn(URL);
        server.downloadReviews(course);
        when(rb.getForText(anyString())).thenThrow(new RuntimeException("Foo"));
        verify(rb, times(1)).getForText(anyString());
    }
}
