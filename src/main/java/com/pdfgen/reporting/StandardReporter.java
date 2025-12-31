package com.pdfgen.reporting;

public class StandardReporter implements Reporter {

    @Override
    public void info(String msg) {
        System.err.println(String.format("INFO: %s", msg));
    }

    @Override
    public void error(String msg) {
        System.err.println(String.format("ERROR: %s", msg));
    }

    @Override
    public void success(String msg) {
        System.err.println(String.format("SUCCESS: %s", msg));
    }

}