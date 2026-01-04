package com.pdfgen.reporting;

public class PrefixedReporter extends MinimalReporter {

    @Override
    public void info(String msg) {
        super.info(String.format("INFO: %s", msg));
    }

    @Override
    public void error(String msg) {
        super.error(String.format("ERROR: %s", msg));
    }

    @Override
    public void success(String msg) {
        super.success(String.format("SUCCESS: %s", msg));
    }

}