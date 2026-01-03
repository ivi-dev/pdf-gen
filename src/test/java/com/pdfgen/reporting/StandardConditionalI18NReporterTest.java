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

import com.pdfgen.ResourceBundleWrapper;

public class StandardConditionalI18NReporterTest {

    private StandardConditionalI18NReporter reporter;

    private PrintStream originalErr;
    
    private ByteArrayOutputStream loggedMsg;

    @BeforeEach
    void setUp() {
        var resBundleWrapMock = mock(ResourceBundleWrapper.class);
        when(resBundleWrapMock.getString("Info")).thenReturn("I18N-Info");
        when(resBundleWrapMock.getString("Info", "arg1")).thenReturn("I18N-Info-arg1");
        when(resBundleWrapMock.getString("Error")).thenReturn("I18N-Error");
        when(resBundleWrapMock.getString("Error", "arg1")).thenReturn("I18N-Error-arg1");
        when(resBundleWrapMock.getString("Success")).thenReturn("I18N-Success");
        when(resBundleWrapMock.getString("Success", "arg1")).thenReturn("I18N-Success-arg1");
        reporter = new StandardConditionalI18NReporter(resBundleWrapMock);
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

    private void assertMessageLogged(
        String prefix, 
        String timestamp, 
        String msg, 
        String arg
    ) {
        var suffix = arg.isEmpty() ? "" : "-" + arg;
        assertEquals(
            String.format(
                "%s: [%s] I18N-%s%s%s", 
                prefix, 
                timestamp, 
                msg, 
                suffix,
                System.lineSeparator()
            ),
            loggedMsg.toString()
        );
    }

    @Test
    void constructorInitializesObject() {
        assertNotNull(reporter);
    }

    @Test
    void infoWritesToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Info";
            reporter.info(msg);
            assertMessageLogged("INFO", timestamp, msg, "");
        }
    }

    @Test
    void infoWritesParameterizedMessageToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Info";
            var arg = "arg1";
            reporter.info(msg, arg);
            assertMessageLogged("INFO", timestamp, msg, arg);
        }
    }

    @Test
    void infoDoesNotWriteToStandardErrWhenNotVerbose() {
        reporter.setVerbose(false);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockLocalDateTime(mockLocalDateTime);
            var msg = "Info";
            reporter.info(msg);
            assertEquals("", loggedMsg.toString()); 
        }
    }

    @Test
    void errorWritesToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Error";
            reporter.error(msg);
            assertMessageLogged("ERROR", timestamp, msg, "");
        }
    }

    @Test
    void errorWritesParameterizedMessageToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Error";
            var arg = "arg1";
            reporter.error(msg, arg);
            assertMessageLogged("ERROR", timestamp, msg, arg);
        }
    }

    @Test
    void errorDoesNotWriteToStandardErrWhenNotVerbose() {
        reporter.setVerbose(false);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockLocalDateTime(mockLocalDateTime);
            var msg = "Error";
            reporter.error(msg);
            assertEquals("", loggedMsg.toString()); 
        }
    }

    @Test
    void successWritesToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Success";
            reporter.success(msg);
            assertMessageLogged("SUCCESS", timestamp, msg, "");
        }
    }

    @Test
    void successWritesParameterizedMessageToStandardErrWhenVerbose() {
        reporter.setVerbose(true);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Success";
            var arg = "arg1";
            reporter.success(msg, arg);
            assertMessageLogged("SUCCESS", timestamp, msg, arg);
        }
    }

    @Test
    void successDoesNotWriteToStandardErrWhenNotVerbose() {
        reporter.setVerbose(false);
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockLocalDateTime(mockLocalDateTime);
            var msg = "Success";
            reporter.success(msg);
            assertEquals("", loggedMsg.toString());
        }
    }

}
