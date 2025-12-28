package com.pdfgen.reporting;

public interface Reporter {

    void reportSuccess(String msg);

    void reportError(String msg);
    
}