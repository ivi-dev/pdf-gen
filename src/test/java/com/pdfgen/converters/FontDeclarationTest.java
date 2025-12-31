package com.pdfgen.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FontDeclarationTest {

    @Test
    void toStringReturnsCorrectStringRepresentation() {
        var fontName = "Font-1";
        var fontPath = "/path/to/font-1.ttf";
        var fontDeclaration = new FontDeclaration(fontName, fontPath);
        assertEquals(
            "\"Font-1 /path/to/font-1.ttf\"", 
            fontDeclaration.toString()
        );
    }

}
