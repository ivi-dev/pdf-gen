package com.pdfgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.pdfgen.converters.FontDeclaration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ArgsTest {

    private static File mockDataFile;

    private static Args parsedArgs;

    private static List<FontDeclaration> fonts = List.of(
        new FontDeclaration(
            "Font-1", 
            "/path/to/font-1.ttf"
        ),
        new FontDeclaration(
            "Font-2", 
            "/path/to/font-2.ttf"
        )
    );

    @BeforeAll
    static void initAll() {
        mockDataFile = mock(File.class);
        when(mockDataFile.getPath()).thenReturn("data.json");
        when(mockDataFile.toString()).thenReturn("/path/to/data.json");
        parsedArgs = new Args(
            "custom-template.html",
            mockDataFile,
            "document.pdf", 
            new Locale("bg"),
            fonts,
            true
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
        assertEquals(2, fonts.size());
        assertEquals("Font-1", fonts.get(0).name());
        assertEquals("/path/to/font-1.ttf", fonts.get(0).path());
        assertEquals("Font-2", fonts.get(1).name());
        assertEquals("/path/to/font-2.ttf", fonts.get(1).path());
    }

    @Test
    void getVerboseReturnsCorrectValue() {
        assertEquals(true, parsedArgs.getVerbose());
    }

    @Test
    void toStringRepresentsNonNullArgumentsWithNonThrowingFieldValueGetter() {
        var args = new Args(
            "custom-template.html",
            mockDataFile,
            null, 
            null,
            fonts,
            true
        );
        assertEquals(
            "--template=custom-template.html, " +
            "--data=/path/to/data.json, " +
            "--font=\"Font-1 /path/to/font-1.ttf\", " +
            "--font=\"Font-2 /path/to/font-2.ttf\", " +
            "--verbose", 
            args.toString()
        );
    }

    @Test
    void toStringRepresentsNonNullArgumentsWithThrowingFieldValueGetter() 
        throws IllegalArgumentException, IllegalAccessException {
        var mockFieldValueGetter = mock(FieldValueGetter.class);
        var callCtr = new AtomicInteger(0);
        doAnswer(invocation -> {
            Field field = invocation.getArgument(0);
            return switch (field.getName()) {
                case "template" -> "custom-template.html";
                case "data"     -> mockDataFile;
                case "output"     -> throw new IllegalAccessException();
                case "locale"     -> {
                    var nCalls = callCtr.incrementAndGet();
                    if (nCalls == 1) {
                        yield new Locale("bg");
                    } else {
                        throw new IllegalAccessException();
                    }
                }
                case "font"     -> fonts;
                case "verbose"  -> false;
                default         -> null;
            };
        }).when(mockFieldValueGetter).get(
            any(Field.class), 
            any(Args.class)
        );
        var args = new Args(
            "custom-template.html",
            mockDataFile,
            "/path/to/document.pdf", 
            new Locale("bg"),
            fonts,
            false,
            mockFieldValueGetter
        );
        assertEquals(
            "--template=custom-template.html, " +
            "--data=/path/to/data.json, " +
            "--font=\"Font-1 /path/to/font-1.ttf\", " +
            "--font=\"Font-2 /path/to/font-2.ttf\"", 
            args.toString()
        );
    }

}
