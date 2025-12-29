package com.pdfgen.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StandardReporterTest {

    private StandardReporter reporter;

    private PrintStream originalErr;
    
    private ByteArrayOutputStream loggedMsg;

    @BeforeEach
    void setUp() {
        reporter = new StandardReporter();
        originalErr = System.err;
        loggedMsg = new ByteArrayOutputStream();
        System.setErr(new PrintStream(loggedMsg));
    }

    @Test
    void infoWritesToStandardErr() {
        try {
            var msg = "Info.";
            reporter.info(msg);
            assertEquals(
                msg + System.lineSeparator(), 
                loggedMsg.toString()
            );
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void errorWritesToStandardErr() {
        try {
            var msg = "Error.";
            reporter.error(msg);
            assertEquals(
                msg + System.lineSeparator(), 
                loggedMsg.toString()
            );
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void successWritesToStandardErr() {
        try {
            var msg = "Success.";
            reporter.success(msg);
            assertEquals(
                msg + System.lineSeparator(), 
                loggedMsg.toString()
            );
        } finally {
            System.setErr(originalErr);
        }
    }

}
