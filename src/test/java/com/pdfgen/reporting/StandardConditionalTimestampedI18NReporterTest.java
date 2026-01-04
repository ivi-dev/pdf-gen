package com.pdfgen.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.pdfgen.ResourceBundleWrapper;

public class StandardConditionalTimestampedI18NReporterTest {

    private StandardConditionalTimestampedI18NReporter reporter;

    private ResourceBundleWrapper resBundle;

    private PrintStream originalErr;
    
    private ByteArrayOutputStream loggedMsg;

    @BeforeEach
    void setUp() {
        resBundle = mock(ResourceBundleWrapper.class);
        when(resBundle.getString(eq("Info."))).thenReturn("Info resource.");
        when(resBundle.getString(eq("Info."), anyString())).thenReturn("Templated info resource.");
        when(resBundle.getString(eq("Error."))).thenReturn("Error resource.");
        when(resBundle.getString(eq("Error."), anyString())).thenReturn("Templated error resource.");
        when(resBundle.getString(eq("Success."))).thenReturn("Success resource.");
        when(resBundle.getString(eq("Success."), anyString())).thenReturn("Templated success resource.");
        reporter = new StandardConditionalTimestampedI18NReporter(resBundle);
        originalErr = System.err;
        loggedMsg = new ByteArrayOutputStream();
        System.setErr(new PrintStream(loggedMsg));
    }

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
    }

    private static String mockLocalDateTime(MockedStatic<LocalDateTime> mockLocalDateTime) {
        var timestamp = "timestamp";
        var mockNow = mock(LocalDateTime.class);
        when(mockNow.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).thenReturn(timestamp);
        mockLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);
        return timestamp;
    }

    private void assertMessageLogged(String prefix, String timestamp, String msg) {
        assertEquals(
            String.format(
                "%s: [%s] %s%s", 
                prefix, 
                timestamp, 
                msg, 
                System.lineSeparator()
            ),
            loggedMsg.toString()
        );
    }

    @Test
    void infoWritesSimpleStringToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Info.";
            reporter.info(msg);
            assertMessageLogged("INFO", timestamp, "Info resource.");
        }
    }

    @Test
    void infoWritesTemplatedStringToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Info.";
            reporter.info(msg, "include");
            assertMessageLogged("INFO", timestamp, "Templated info resource.");
        }
    }

    @Test
    void infoDoesNotWriteToStandardErrWhenNotVerbose() {
        reporter.setVerbose(false);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockLocalDateTime(mockLocalDateTime);
            var msg = "Info.";
            reporter.info(msg);
            assertEquals("", loggedMsg.toString()); 
        }
    }

    @Test
    void errorWritesSimpleStringToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Error.";
            reporter.error(msg);
            assertMessageLogged("ERROR", timestamp, "Error resource.");
        }
    }

    @Test
    void errorDoesNotWriteToStandardErrWhenNotVerbose() {
        reporter.setVerbose(false);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockLocalDateTime(mockLocalDateTime);
            var msg = "Error.";
            reporter.error(msg);
            assertEquals("", loggedMsg.toString()); 
        }
    }

    @Test
    void errorWritesTemplatedStringToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Error.";
            reporter.error(msg, "include");
            assertMessageLogged("ERROR", timestamp, "Templated error resource.");
        }
    }

    @Test
    void successWritesSimpleStringToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Success.";
            reporter.success(msg);
            assertMessageLogged("SUCCESS", timestamp, "Success resource.");
        }
    }

    @Test
    void successDoesNotWriteToStandardErrWhenNotVerbose() {
        reporter.setVerbose(false);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockLocalDateTime(mockLocalDateTime);
            var msg = "Success.";
            reporter.success(msg);
            assertEquals("", loggedMsg.toString());
        }
    }

    @Test
    void successWritesTemplatedStringToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Success.";
            reporter.success(msg, "include");
            assertMessageLogged("SUCCESS", timestamp, "Templated success resource.");
        }
    }

}
