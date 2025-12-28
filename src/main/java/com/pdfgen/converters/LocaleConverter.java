package com.pdfgen.converters;

import java.util.Locale;

import com.beust.jcommander.IStringConverter;

public class LocaleConverter implements IStringConverter<java.util.Locale> {

    @Override
    public Locale convert(String value) {
        String[] dashParts = value.split("-");
        if (dashParts.length >= 2) { // Assume dash is used as separator (BCP 47)
            return new Locale(dashParts[0], dashParts[1]);
        } else {
            var underParts = dashParts[0].split("_");
            if (underParts.length >= 2) { // Assume underscore is used as separator (legacy style)
                return new Locale(underParts[0], underParts[1]);
            }
        }
        if (value.equals("")) {
            return Locale.getDefault();
        } else {
            return new Locale(value);
        }
    }

}
