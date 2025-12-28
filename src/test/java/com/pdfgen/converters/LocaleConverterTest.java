package com.pdfgen.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class LocaleConverterTest {

    @Test
    void convertParsesLanguageOnlyLocale() {
        var converter = new LocaleConverter();
        var locale = converter.convert("en");
        assertEquals("en", locale.getLanguage());
    }

    @Test
    void convertParsesLanguageAndCountryLocaleUnderscoreSeparator() {
        var converter = new LocaleConverter();
        var locale = converter.convert("en_US");
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
    }

    @Test
    void convertParsesLanguageAndCountryLocaleDashSeparator() {
        var converter = new LocaleConverter();
        var locale = converter.convert("en-US");
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
    }

    @Test
    void convertParsesAnEmptyString() {
        var converter = new LocaleConverter();
        var locale = converter.convert("");
        assertEquals(Locale.getDefault().getLanguage(), locale.getLanguage());
    }

}
