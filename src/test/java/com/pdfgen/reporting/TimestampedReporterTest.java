package com.pdfgen.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

public class TimestampedReporterTest {

    private TimestampedReporter reporter;

    private PrintStream originalErr;
    
    private ByteArrayOutputStream loggedMsg;

    @BeforeEach
    void setUp() {
        reporter = new TimestampedReporter();
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
                "[%s] - %s: %s%s", 
                timestamp, 
                prefix, 
                msg, 
                System.lineSeparator()
            ),
            loggedMsg.toString()
        );
    }

    @Test
    void infoWritesToStandardErr() {
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Info.";
            reporter.info(msg);
            assertMessageLogged("INFO", timestamp, msg);
        }
    }

    @Test
    void errorWritesToStandardErr() {
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Error.";
            reporter.error(msg);
            assertMessageLogged("ERROR", timestamp, msg);
        }
    }

    @Test
    void successWritesToStandardErr() {
        try (var mockLocalDateTime = mockStatic(LocalDateTime.class)) {
            var timestamp = mockLocalDateTime(mockLocalDateTime);
            var msg = "Success.";
            reporter.success(msg);
            assertMessageLogged("SUCCESS", timestamp, msg);
        }
    }

}
