package com.pdfgen.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StandardOutReporterTest {

    @Test
    void reportSuccessWritesToStandardOutput() {
        var reporter = new StandardOutReporter();
        var originalOut = System.out;
        var outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        try {
            var msg = "Success!";
            reporter.reportSuccess(msg);
            assertEquals(msg + System.lineSeparator(), outContent.toString());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void reportErrorWritesToStandardOutput() {
        var reporter = new StandardOutReporter();
        var originalOut = System.err;
        var outContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outContent));
        try {
            var msg = "Error!";
            reporter.reportError(msg);
            assertEquals(msg + System.lineSeparator(), outContent.toString());
        } finally {
            System.setErr(originalOut);
        }
    }

}
