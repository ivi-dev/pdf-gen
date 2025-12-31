package com.pdfgen.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.beust.jcommander.ParameterException;

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
        var exception = assertThrows(
            ParameterException.class, 
            () -> converter.convert("").getLanguage()
        );
        assertEquals(
            "A blank locale identifier provided.", 
            exception.getMessage()
        );
    }

}
