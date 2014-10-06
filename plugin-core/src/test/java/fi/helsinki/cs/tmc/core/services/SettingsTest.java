package fi.helsinki.cs.tmc.core.services;

import fi.helsinki.cs.tmc.core.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class SettingsTest {
    
    private static final String CORRECT = "correct";

    private Preferences prefs;
    private Settings settings;

    @Before
    public void setUp() {

        prefs = mock(Preferences.class);
        settings = new Settings(prefs);
        when(prefs.getBoolean(Settings.PREF_KEY_SAVE_PASSWORD, true)).thenReturn(true);
    }

    @Test
    public void currentCourseReturnsCorrectValueWhenSet() {

        when(prefs.get(Settings.PREF_KEY_CURRENT_COURSE, "")).thenReturn(CORRECT);
        assertEquals(CORRECT, settings.getCurrentCourseName());
        verify(prefs, times(1)).get(Settings.PREF_KEY_CURRENT_COURSE, "");
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void canSaveCurrentCourse() {

        settings.setCurrentCourseName("kurssi");
        verify(prefs, times(1)).put(Settings.PREF_KEY_CURRENT_COURSE, "kurssi");
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void serverBaseUrlReturnsCorrectValueWhenSet() {

        when(prefs.get(Settings.PREF_KEY_TMC_SERVER_URL, "")).thenReturn(CORRECT);
        assertEquals(CORRECT, settings.getServerBaseUrl());
        verify(prefs, times(1)).get(Settings.PREF_KEY_TMC_SERVER_URL, "");
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void canSaveServerBaseUrl() {

        settings.setServerBaseUrl("http://tmc.mooc.fi");
        verify(prefs, times(1)).put(Settings.PREF_KEY_TMC_SERVER_URL, "http://tmc.mooc.fi");
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void usernameReturnsCorrectValueWhenSet() {

        when(prefs.get(Settings.PREF_KEY_USERNAME, "")).thenReturn(CORRECT);
        assertEquals(CORRECT, settings.getUsername());
        verify(prefs, times(1)).get(Settings.PREF_KEY_USERNAME, "");
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void canSaveUsername() {

        settings.setUsername("matti.meikalainen");
        verify(prefs, times(1)).put(Settings.PREF_KEY_USERNAME, "matti.meikalainen");
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void passwordReturnsCorrectValueWhenSet() {

        when(prefs.get(Settings.PREF_KEY_PASSWORD, "")).thenReturn(CORRECT);
        assertEquals(CORRECT, settings.getPassword());
        verify(prefs, times(1)).get(Settings.PREF_KEY_PASSWORD, "");
    }

    @Test
    public void canSavePassword() {

        settings.setPassword("password123");
        verify(prefs, times(1)).put(Settings.PREF_KEY_PASSWORD, "password123");
    }

    @Test
    public void isCheckingForUpdatesInTheBackgroundReturnCorrectValueWhenSet() {

        when(prefs.getBoolean(Settings.PREF_KEY_CHECK_FOR_UPDATES_IN_BACKGROUND, true)).thenReturn(true);
        assertEquals(true, settings.isCheckingForUpdatesInTheBackground());
        verify(prefs, times(1)).getBoolean(Settings.PREF_KEY_CHECK_FOR_UPDATES_IN_BACKGROUND, true);
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void canSaveCheckingForUpdates() {

        settings.setCheckingForUpdatesInTheBackground(true);
        verify(prefs, times(1)).putBoolean(Settings.PREF_KEY_CHECK_FOR_UPDATES_IN_BACKGROUND, true);
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void isCheckingForUnopenedAtStartupReturnCorrectValueWhenSet() {

        when(prefs.getBoolean(Settings.PREF_KEY_CHECK_FOR_UNOPENED_AT_STARTUP, true)).thenReturn(true);
        assertEquals(true, settings.isCheckingForUnopenedAtStartup());
        verify(prefs, times(1)).getBoolean(Settings.PREF_KEY_CHECK_FOR_UNOPENED_AT_STARTUP, true);
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void canSaveCheckingForUnopened() {

        settings.setCheckingForUnopenedAtStartup(true);
        verify(prefs, times(1)).putBoolean(Settings.PREF_KEY_CHECK_FOR_UNOPENED_AT_STARTUP, true);
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void isSpywareEnabledReturnCorrectValueWhenSet() {

        when(prefs.getBoolean(Settings.PREF_KEY_SPYWARE_ENABLED, true)).thenReturn(true);
        assertEquals(true, settings.isSpywareEnabled());
        verify(prefs, times(1)).getBoolean(Settings.PREF_KEY_SPYWARE_ENABLED, true);
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void canSaveSpywareEnabled() {

        settings.setIsSpywareEnabled(true);
        verify(prefs, times(1)).putBoolean(Settings.PREF_KEY_SPYWARE_ENABLED, true);
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void isDetailedSpywareEnabledReturnCorrectValueWhenSet() {

        when(prefs.getBoolean(Settings.PREF_KEY_DETAILED_SPYWARE_ENABLED, true)).thenReturn(true);
        assertEquals(true, settings.isDetailedSpywareEnabled());
        verify(prefs, times(1)).getBoolean(Settings.PREF_KEY_DETAILED_SPYWARE_ENABLED, true);
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void canSaveDetailedSpywareEnabled() {

        settings.setIsDetailedSpywareEnabled(true);
        verify(prefs, times(1)).putBoolean(Settings.PREF_KEY_DETAILED_SPYWARE_ENABLED, true);
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void exerciseFilePathReturnsCorrectValueWhenSet() {

        when(prefs.get(Settings.PREF_KEY_EXERCISE_FILEPATH, "")).thenReturn("path");
        assertEquals("path", settings.getExerciseFilePath());
        verify(prefs, times(1)).get(Settings.PREF_KEY_EXERCISE_FILEPATH, "");
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void canSaveExerciseFilePath() throws IOException {

        settings.setExerciseFilePath("path");
        verify(prefs, times(1)).put(Settings.PREF_KEY_EXERCISE_FILEPATH, FileUtil.getUnixPath(new File("path").getCanonicalPath()));
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void exerciseFilePathMustNotBeNull() {

        settings.setExerciseFilePath(null);
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void testSavingFailure() throws BackingStoreException {

        settings.setUsername("matti.meikalainen");
        settings.setPassword("password123");

        doThrow(new BackingStoreException("")).when(prefs).flush();
        assertFalse(settings.save());
    }

    @Test
    public void testSavingSuccess() {

        settings.setUsername("matti.meikalainen");
        settings.setPassword("password123");

        assertTrue(settings.save());
    }

    @Test
    public void testDefaultSettingsSuccess() {

        assertNotNull(Settings.getDefaultSettings());
    }

    @Test
    public void defaultLocaleNumReturnCorrectValue() {

        assertEquals(Settings.DEFAULT_LOCALE_NUM, settings.getDefaultLocaleNum());
    }

    @Test
    public void availableLocalesReturnsCorrectValue() {

        assertEquals(Settings.AVAILABLE_LOCALES.length, settings.getAvailableLocales().length);
        for (int i = 0; i < Settings.AVAILABLE_LOCALES.length; i++) {
            assertEquals(Settings.AVAILABLE_LOCALES[i], settings.getAvailableLocales()[i]);
        }
    }

    @Test
    public void errorMsgLocaleNumReturnsCorrectValue() {

        when(prefs.getInt(Settings.PREF_ERROR_MSG_LOCALE, Settings.DEFAULT_LOCALE_NUM)).thenReturn(2);
        assertEquals(2, settings.getErrorMsgLocaleNum());
        verify(prefs, times(1)).getInt(Settings.PREF_ERROR_MSG_LOCALE, Settings.DEFAULT_LOCALE_NUM);
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void canSaveErrorMsgLocale() {

        settings.setErrorMsgLocale(1);
        verify(prefs, times(1)).putInt(Settings.PREF_ERROR_MSG_LOCALE, 1);
        verifyNoMoreInteractions(prefs);
    }

    @Test
    public void errorMsgLocaleReturnsCorrectValue() {

        when(prefs.getInt(Settings.PREF_ERROR_MSG_LOCALE, Settings.DEFAULT_LOCALE_NUM)).thenReturn(0);
        assertEquals(new Locale("English"), settings.getErrorMsgLocale());

        when(prefs.getInt(Settings.PREF_ERROR_MSG_LOCALE, Settings.DEFAULT_LOCALE_NUM)).thenReturn(1);
        assertEquals(new Locale("Finnish"), settings.getErrorMsgLocale());
    }
}
