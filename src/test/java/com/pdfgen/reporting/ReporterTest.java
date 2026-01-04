package com.pdfgen.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MockReporter implements Reporter {

    @Override
    public void info(String msg) {
        System.err.println(msg);
    }

    @Override
    public void error(String msg) {
        System.err.println(msg);
    }

    @Override
    public void success(String msg) {
        System.err.println(msg);
    }

}

public class ReporterTest {

    private Reporter reporter;
            
    private PrintStream originalErr;
    
    private ByteArrayOutputStream loggedMsg;

    @BeforeEach
    void setUp() {
        reporter = new MockReporter();
        originalErr = System.err;
        loggedMsg = new ByteArrayOutputStream();
        System.setErr(new PrintStream(loggedMsg));
    }

    @Test
    void infoWritesTemplatedStringToStandardErr() {
        try {
            var msg = "Hello, {0}!";
            reporter.info(msg, "world");
            assertEquals(
                String.format(
                    "Hello, world!%s", 
                    System.lineSeparator()
                ), 
                loggedMsg.toString()
            );
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void infoWritesSimpleStringToStandardErr() {
        try {
            var msg = "Hello!";
            reporter.info(msg);
            assertEquals(
                String.format(
                    "Hello!%s", 
                    System.lineSeparator()
                ), 
                loggedMsg.toString()
            );
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void errorWritesTemplatedStringToStandardErr() {
        try {
            var msg = "Hello, {0}!";
            reporter.error(msg, "world");
            assertEquals(
                String.format(
                    "Hello, world!%s", 
                    System.lineSeparator()
                ), 
                loggedMsg.toString()
            );
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void errorWritesSimpleStringToStandardErr() {
        try {
            var msg = "Hello!";
            reporter.error(msg);
            assertEquals(
                String.format(
                    "Hello!%s", 
                    System.lineSeparator()
                ), 
                loggedMsg.toString()
            );
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void successWritesTemplatedStringToStandardErr() {
        try {
            var msg = "Hello, {0}!";
            reporter.success(msg, "world");
            assertEquals(
                String.format(
                    "Hello, world!%s", 
                    System.lineSeparator()
                ), 
                loggedMsg.toString()
            );
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void successWritesSimpleStringToStandardErr() {
        try {
            var msg = "Hello!";
            reporter.success(msg);
            assertEquals(
                String.format(
                    "Hello!%s", 
                    System.lineSeparator()
                ), 
                loggedMsg.toString()
            );
        } finally {
            System.setErr(originalErr);
        }
    }

}
