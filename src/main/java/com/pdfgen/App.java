package com.pdfgen;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static void disableLogging() {
        // 1. Force Commons Logging (used by PDFBox) to do nothing
        System.setProperty(
            "org.apache.commons.logging.Log", 
            "org.apache.commons.logging.impl.NoOpLog"
        );
        // 2. Silence the specific Java Util Logging (JUL) package
        Logger.getLogger("org.apache.pdfbox").setLevel(Level.OFF);
        Logger.getLogger("org.apache.fontbox").setLevel(Level.OFF);
    }

    private App() { }

    public static void main(String[] args) {
        if (!Arrays.asList(args).contains("--verbose")) {
            disableLogging();
        }
        new Proc(args).run();
    }
    
}
