package com.pdfgen.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.beust.jcommander.ParameterException;

public class FontConverterTest {

    @Test
    void convertThrowsExcepitonOnNull() {
        var converter = new FontConverter();
        var exception = assertThrows(ParameterException.class, () -> converter.convert(null));
        assertEquals(
            "A font declaration must contain both a name and a file path!", 
            exception.getMessage()
        );
        assertEquals(
            "Cannot invoke \"String.split(String)\" because \"value\" is null", 
            exception.getCause().getMessage()
        );
    }

    @Test
    void convertThrowsExcepitonOnEmptyString() {
        var converter = new FontConverter();
        var exception = assertThrows(ParameterException.class, () -> converter.convert(""));
        assertEquals("A font declaration must contain both a name and a file path!", exception.getMessage());
    }

    @Test
    void convertThrowsExcepitonOnMissingData() {
        var converter = new FontConverter();
        var exception = assertThrows(ParameterException.class, () -> converter.convert("Font-1"));
        assertEquals("A font declaration must contain both a name and a file path!", exception.getMessage());
    }

    @Test
    void convertConvertsToFont() {
        var converter = new FontConverter();
        var fontName = "Font-1";
        var fontPath = "/path/to/font-1.ttf";
        assertEquals(
            new FontDeclaration(fontName, fontPath), 
            converter.convert(fontName + " " + fontPath)
        );
    }

}
