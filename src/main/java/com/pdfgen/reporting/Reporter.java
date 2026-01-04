package com.pdfgen.reporting;

import java.text.MessageFormat;

public interface Reporter {

    void info(String msg);
    
    void error(String msg);

    void success(String msg);
    
    default void info(String msg, Object ...args) {
        info(MessageFormat.format(msg, args));
    }

    default void error(String msg, Object ...args) {
        error(MessageFormat.format(msg, args));
    }

    default void success(String msg, Object ...args) {
        success(MessageFormat.format(msg, args));
    }

}