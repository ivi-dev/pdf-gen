package com.pdfgen.reporting.prefixers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.pdfgen.reporting.Level;

public class TimestampPrefixer implements Prefixer {

    private static String now() {
        return LocalDateTime.now().format(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );
    }

    @Override
    public String get(Level level) {
        return String.format("[%s]", now());
    }

}