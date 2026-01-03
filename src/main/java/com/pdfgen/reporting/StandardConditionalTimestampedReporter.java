package com.pdfgen.reporting;

public class StandardConditionalTimestampedReporter 
    extends TimestampedReporter 
    implements ConditionalReporter {

    private boolean verbose = false;

    public StandardConditionalTimestampedReporter() {
        super();
    }

    StandardConditionalTimestampedReporter(boolean verbose) {
        super();
        this.verbose = verbose;
    }

    @Override
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void info(String msg) {
        if (verbose) super.info(msg);
    }

    @Override
    public void error(String msg) {
        if (verbose) super.error(msg);
    }

    @Override
    public void success(String msg) {
        if (verbose) super.success(msg);
    }

}