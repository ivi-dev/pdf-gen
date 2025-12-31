package com.pdfgen.reporting;

public interface ConditionalI18NReporter extends ConditionalReporter {

    void info(String msg, Object... args);

    void success(String msg, Object... args);

    void error(String msg, Object... args);

}