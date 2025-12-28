package com.pdfgen.reporting;

public class StandardOutReporter implements Reporter {

    @Override
    public void reportSuccess(String msg) {
        System.out.println(msg);
    }

    @Override
    public void reportError(String msg) {
        System.err.println(msg);
    }

}