package com.pdfgen.reporting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampedReporter extends StandardReporter {

    private static String now() {
        return LocalDateTime.now().format(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );
    }

    private static String line(String prefix, String msg) {
        return String.format("[%s] - %s: %s", now(), prefix, msg);
    }

    @Override
    public void info(String msg) {
        super.info(line("INFO", msg));
    }

    @Override
    public void error(String msg) {
        super.error(line("ERROR", msg));
    }

    @Override
    public void success(String msg) {
        super.success(line("SUCCESS", msg));
    }

}