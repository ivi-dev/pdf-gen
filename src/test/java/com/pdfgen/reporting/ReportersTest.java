package com.pdfgen.reporting;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReportersTest {

    private Reporters reporters;

    private Reporter reporter1 = mock(Reporter.class);

    @BeforeEach
    void setUp() {
        reporter1 = mock(Reporter.class);
        var reportersMap = new HashMap<String, Reporter>();
        reportersMap.put("reporter-1", reporter1);
        reporters = new Reporters(reportersMap);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testInvoke() {
        var proc = mock(Consumer.class);
        reporters.invoke("reporter-1", proc);
        verify(proc).accept(reporter1);
    }

    @Test
    void testInfo() {
        reporters.info("reporter-1", "Hello, {0}!", "world");
        verify(reporter1).info("Hello, {0}!", "world");
    }

    @Test
    void testError() {
        reporters.error("reporter-1", "Hello, {0}!", "world");
        verify(reporter1).error("Hello, {0}!", "world");
    }

    @Test
    void testSuccess() {
        reporters.success("reporter-1", "Hello, {0}!", "world");
        verify(reporter1).success("Hello, {0}!", "world");
    }

}
