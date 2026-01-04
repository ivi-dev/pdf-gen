package com.pdfgen.reporting;

import java.util.Map;
import java.util.function.Consumer;

public class Reporters {

    private Map<String, Reporter> reporters;

    public Reporters(Map<String, Reporter> reporters) {
        this.reporters = reporters;
    }

    public void invoke(String reporterName, Consumer<Reporter> proc) {
        proc.accept(reporters.get(reporterName));
    }

    public void info(String reporterName, String msg, Object ...args) {
        reporters.get(reporterName).info(msg, args);
    }

    public void error(String reporterName, String msg, Object ...args) {
        reporters.get(reporterName).error(msg, args);
    }

    public void success(String reporterName, String msg, Object ...args) {
        reporters.get(reporterName).success(msg, args);
    }

}