package com.pdfgen.reporting.prefixers;

import com.pdfgen.reporting.Level;

public class LevelPrefixer implements Prefixer {

    @Override
    public String get(Level level) {
        return String.format("%s:", level.name());
    }

}