package com.pdfgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.pdfgen.converters.FontDeclaration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ArgsTest {

    private static File mockDataFile;

    private static Args parsedArgs;

    @BeforeAll
    static void initAll() {
        mockDataFile = mock(File.class);
        when(mockDataFile.getPath()).thenReturn("data.json");
        parsedArgs = new Args(
            "custom-template.html",
            mockDataFile,
            "document.pdf", 
            new Locale("bg"),
            List.of(
                new FontDeclaration("Font-1", "/path/to/font-1.ttf")
            )
        );
    } 

    @Test
    void parameterlessConstructorInitializesObject() {
        assertNotNull(new Args());
    }
    
    @Test
    void getTemplateReturnsCorrectValue() {
        assertEquals("custom-template.html", parsedArgs.getTemplate());
    }

    @Test
    void getDataReturnsCorrectValue() {
        assertEquals("data.json", parsedArgs.getData().getPath());
    }

    @Test
    void getOutputReturnsCorrectValue() {
        assertEquals("document.pdf", parsedArgs.getOutput());
    }

    @Test
    void getLocaleReturnsCorrectValue() {
        assertEquals("bg", parsedArgs.getLocale().toString());
    }

    @Test
    void getFontReturnsCorrectValue() {
        var fonts = parsedArgs.getFont();
        assertEquals(1, fonts.size());
        assertEquals("Font-1", fonts.get(0).name());
        assertEquals("/path/to/font-1.ttf", fonts.get(0).path());
    }

}
