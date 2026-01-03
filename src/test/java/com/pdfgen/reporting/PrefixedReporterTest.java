package com.pdfgen.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrefixedReporterTest {

    private PrefixedReporter reporter;
            
    private PrintStream originalErr;
    
    private ByteArrayOutputStream loggedMsg;

    @BeforeEach
    void setUp() {
        reporter = new PrefixedReporter();
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
                String.format(
                    "INFO: %s%s", 
                    msg, 
                    System.lineSeparator()
                ), 
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
                String.format(
                    "ERROR: %s%s", 
                    msg, 
                    System.lineSeparator()
                ), 
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
                String.format(
                    "SUCCESS: %s%s", 
                    msg, 
                    System.lineSeparator()
                ), 
                loggedMsg.toString()
            );
        } finally {
            System.setErr(originalErr);
        }
    }

}
