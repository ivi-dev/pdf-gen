package com.pdfgen.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractReporterTest<T extends Reporter> {

    protected T reporter;

    protected PrintStream originalErr;

    protected ByteArrayOutputStream loggedMsg;
    
    protected static final String INFO_KEY = "info";

    protected static final String ERROR_KEY = "error";

    protected static final String SUCCESS_KEY = "success";

    protected abstract T getReporter();

    protected String getLineFormat() {
        return "%s";
    }

    @BeforeEach
    void setUp() {
        reporter = getReporter();
        originalErr = System.err;
        loggedMsg = new ByteArrayOutputStream();
        System.setErr(new PrintStream(loggedMsg));
    }

    private static Object[] getMessageParts(Object ...parts) {
        var parts_ = new ArrayList<>(Arrays.asList(parts));
        parts_.add(System.lineSeparator()); 
        return parts_.toArray();
    }

    protected void assertMessageLoggedWithFormat(String fmt, Object ...parts) {
        assertEquals(
            String.format(
                fmt + "%s", 
                getMessageParts(parts)
            ),
            loggedMsg.toString()
        );
        loggedMsg.reset();
    }

    protected void assertMessageLogged(Object ...parts) {
        assertMessageLoggedWithFormat(getLineFormat(), parts);
    }

    protected void assertMessageNotLogged() {
        assertEquals("", loggedMsg.toString());
    }

    protected void assertPlainLogging() {
        reporter.info(INFO_KEY);
        assertMessageLogged(INFO_KEY);
        reporter.error(ERROR_KEY);
        assertMessageLogged(ERROR_KEY);
        reporter.success(SUCCESS_KEY);
        assertMessageLogged(SUCCESS_KEY);
    }

    protected void assertLoggingSilent() {
        reporter.info(INFO_KEY);
        assertMessageNotLogged();
        reporter.error(ERROR_KEY);
        assertMessageNotLogged();
        reporter.success(SUCCESS_KEY);
        assertMessageNotLogged();
    }

    protected void assertPrefixedLogging(String prefix) {
        reporter.info(INFO_KEY);
        assertMessageLogged(prefix + " " + INFO_KEY);
        reporter.error(ERROR_KEY);
        assertMessageLogged(prefix + " " + ERROR_KEY);
        reporter.success(SUCCESS_KEY);
        assertMessageLogged(prefix + " " + SUCCESS_KEY);
    }

    protected void assertModifiedLogging(String msg) {
        reporter.info(INFO_KEY);
        assertMessageLogged(msg);
        reporter.error(ERROR_KEY);
        assertMessageLogged(msg);
        reporter.success(SUCCESS_KEY);
        assertMessageLogged(msg);
    }

    protected void assertPrefixedModifiedLogging(
        String prefix, 
        String msg
    ) {
        reporter.info(INFO_KEY);
        assertMessageLogged(prefix + " " + msg);
        reporter.error(ERROR_KEY);
        assertMessageLogged(prefix + " " + msg);
        reporter.success(SUCCESS_KEY);
        assertMessageLogged(prefix + " " + msg);
    }

}