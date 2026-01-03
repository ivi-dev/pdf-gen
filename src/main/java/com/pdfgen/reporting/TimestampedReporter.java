package com.pdfgen.reporting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampedReporter extends StandardReporter {

    private static String now() {
        return LocalDateTime.now().format(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );
    }

    @Override
    public void info(String msg) {
        super.info(String.format("[%s] %s", now(), msg));
    }

    @Override
    public void error(String msg) {
        super.error(String.format("[%s] %s", now(), msg));
    }

    @Override
    public void success(String msg) {
        super.success(String.format("[%s] %s", now(), msg));
    }

}