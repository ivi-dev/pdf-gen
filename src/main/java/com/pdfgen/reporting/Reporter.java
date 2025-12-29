package com.pdfgen.reporting;

public interface Reporter {

    void info(String msg);

    void error(String msg);

    void success(String msg);
    
}