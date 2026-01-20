package com.pdfgen.reporting;

public interface Reporter {

    void info(String msg, Object ...args);

    void error(String msg, Object ...args);

    void success(String msg, Object ...args);

}