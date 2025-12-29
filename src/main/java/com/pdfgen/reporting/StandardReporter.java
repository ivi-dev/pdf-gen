package com.pdfgen.reporting;

public class StandardReporter implements Reporter {

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