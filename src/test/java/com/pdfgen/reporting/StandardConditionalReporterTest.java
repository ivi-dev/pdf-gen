package com.pdfgen.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

public class StandardConditionalReporterTest {

    private StandardConditionalReporter reporter;

    private PrintStream originalErr;
    
    private ByteArrayOutputStream loggedMsg;

    @BeforeEach
    void setUp() {
        reporter = new StandardConditionalReporter();
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
    void minimalConstructorInitializesObject() {
        assertNotNull(reporter);
    }

    @Test
    void secondaryConstructorInitializesObject() {
        var reporter = new StandardConditionalReporter(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Info.";
            reporter.info(msg);
            assertMessageLogged("INFO", timestamp, msg);
        }
    }

    @Test
    void infoWritesToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Info.";
            reporter.info(msg);
            assertMessageLogged("INFO", timestamp, msg);
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
    void errorWritesToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Error.";
            reporter.error(msg);
            assertMessageLogged("ERROR", timestamp, msg);
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
    void successWritesToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Success.";
            reporter.success(msg);
            assertMessageLogged("SUCCESS", timestamp, msg);
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
    void setVerboseChangesVerbosity() {
        reporter.setVerbose(false);
        reporter.info("Info.");
        assertEquals("", loggedMsg.toString());
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Success.";
            reporter.success(msg);
            assertMessageLogged("SUCCESS", timestamp, msg);
        }
    }

}
