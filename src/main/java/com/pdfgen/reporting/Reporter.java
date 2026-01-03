package com.pdfgen.reporting;

import java.text.MessageFormat;

public interface Reporter {

    void info(String msg);
    
    void error(String msg);

    void success(String msg);
    
    default void info(String msg, Object ...args) {
        info(getLine(msg, args));
    }

    default void error(String msg, Object ...args) {
        error(getLine(msg, args));
    }

    default void success(String msg, Object ...args) {
        success(getLine(msg, args));
    }

    private static String getLine(String msg, Object ...args) {
        return args.length > 0 ? 
               MessageFormat.format(msg, args) : 
               msg;
    }

}