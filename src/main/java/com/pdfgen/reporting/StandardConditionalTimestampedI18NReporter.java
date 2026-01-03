package com.pdfgen.reporting;

import com.pdfgen.ResourceBundleWrapper;

public class StandardConditionalTimestampedI18NReporter 
    extends StandardConditionalTimestampedReporter {

    private ResourceBundleWrapper resBundle;

    public StandardConditionalTimestampedI18NReporter(
        ResourceBundleWrapper resBundle
    ) {
        super();
        this.resBundle = resBundle;
    }

    @Override
    public void info(String msg) {
        super.info(resBundle.getString(msg));
    }

    @Override
    public void success(String msg) {
        super.success(resBundle.getString(msg));
    }

    @Override
    public void error(String msg) {
        super.error(resBundle.getString(msg));
    }

    @Override
    public void info(String msg, Object ...args) {
        super.info(resBundle.getString(msg, args));
    }

    @Override
    public void success(String msg, Object ...args) {
        super.success(resBundle.getString(msg, args));
    }

    @Override
    public void error(String msg, Object ...args) {
        super.error(resBundle.getString(msg, args));
    }

}