package com.pdfgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.FSUriResolver;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.pdfgen.converters.FontDeclaration;
import com.pdfgen.reporting.ConditionalI18NReporter;

record UseFontArgs(
    @SuppressWarnings("rawtypes") List<FSSupplier> fsSuppliers, 
    List<String> fontFamilies
) { }

public class DefaultDocumentBuilderTest {

    private File mockDataFile;

    private Streams mockStreams;
    
    private PdfRendererBuilder mockPdfBuilder;

    private DataParser mockDataParser;

    private DataPopulator mockDataPopulator;

    private ConditionalI18NReporter mockReporter = mock(ConditionalI18NReporter.class);

    private org.w3c.dom.Document w3cDoc;
    
    private FileOutputStream mockOutputStream;

    private static final List<String[]> DEFAULT_FONTS = List.of(
        new String[] { "Light", "/fonts/NotoSans-Light.ttf" },
        new String[] { "Regular", "/fonts/NotoSans-Regular.ttf" },
        new String[] { "Medium", "/fonts/NotoSans-Medium.ttf" }
    );

    private static final List<String[]> CUSTOM_FONTS = List.of(
        new String[] { "Light", "/path/to/custom-light.ttf" },
        new String[] { "Regular", "/path/to/custom-regular.ttf" },
        new String[] { "Medium", "/path/to/custom-medium.ttf" }
    );

    @SuppressWarnings("unchecked")
    private static void verifyUseFont(
        PdfRendererBuilder mockPdfBuilder, 
        Streams mockStreams,
        int expectedNumCalls,
        List<String[]> controlFonts,
        BiConsumer<Streams, String> argVerify
    ) {
        var fsSupplierCaptor = ArgumentCaptor.forClass(FSSupplier.class);
        var fontNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockPdfBuilder, times(expectedNumCalls)).useFont(
            fsSupplierCaptor.capture(), 
            fontNameCaptor.capture()
        );
        var fsSuppliers = fsSupplierCaptor.getAllValues();
        var fontNames = fontNameCaptor.getAllValues();
        assertEquals(expectedNumCalls, fsSuppliers.size());
        assertEquals(expectedNumCalls, fontNames.size());
        for (var i = 0; i < fsSuppliers.size(); i++) {
            fsSuppliers.get(i).supply();
            argVerify.accept(mockStreams, controlFonts.get(i)[1]);
            assertEquals(controlFonts.get(i)[0], fontNames.get(i));
        }
    }

    private static List<FontDeclaration> declareFonts(List<String[]> fonts) {
        return fonts.stream().map((font) -> new FontDeclaration(font[0], font[1])).toList();
    }

    private DefaultDocumentBuilder initDocBuilder(List<FontDeclaration> font) {
        return new DefaultDocumentBuilder(
            mockDataFile, 
            font,
            mockStreams,
            mockPdfBuilder,
            mockDataParser,
            mockDataPopulator,
            mockReporter
        );
    }

    @BeforeEach 
    void setUp() { 
        mockDataFile = mock(File.class);
        mockStreams = mock(Streams.class);
        mockPdfBuilder = mock(PdfRendererBuilder.class);
        mockDataParser = mock(DataParser.class);
        mockDataPopulator = mock(DataPopulator.class);
        w3cDoc = mock(org.w3c.dom.Document.class);
        mockOutputStream = mock(FileOutputStream.class);
    }

    @Test
    void minimalConstructorInitializesObject() {
        assertNotNull(
            new DefaultDocumentBuilder(
                mockDataFile, 
                new Locale("bg"), 
                null
            )
        );
    }

    @Test
    void initAppliesDefaultFonts() {
        var docBuilder = initDocBuilder(null);
        docBuilder.init(w3cDoc, mockOutputStream);
        verifyUseFont(
            mockPdfBuilder, 
            mockStreams, 
            3, 
            DEFAULT_FONTS,
            (streams, fontPath) -> verify(streams).loadResource(fontPath)
        );
    }

    @Test
    void initAppliesCustomFonts() {
        var docBuilder = initDocBuilder(declareFonts(CUSTOM_FONTS));
        docBuilder.init(w3cDoc, mockOutputStream);
        verifyUseFont(
            mockPdfBuilder, 
            mockStreams, 
            3, 
            CUSTOM_FONTS,
            (streams, fontPath) -> {
                try {
                    verify(streams).newFileInputStream(fontPath);
                } catch (FileNotFoundException e) { }
            }
        );
    }

    @Test
    void initThrowsWhenApplyingCustomFonts() throws FileNotFoundException {
        var docBuilder = initDocBuilder(declareFonts(CUSTOM_FONTS));
        doThrow(new FileNotFoundException("File not found!"))
            .when(mockStreams)
            .newFileInputStream(CUSTOM_FONTS.get(0)[1]);
        var exception = assertThrows(
            RuntimeException.class, 
            () -> {
                docBuilder.init(w3cDoc, mockOutputStream);
                verifyUseFont(
                    mockPdfBuilder, 
                    mockStreams, 
                    3, 
                    CUSTOM_FONTS,
                    (streams, fontPath) -> {
                        try {
                            verify(streams).newFileInputStream(fontPath);
                        } catch (FileNotFoundException e) { }
                    }
                );
            }
        );
        assertEquals(
            "Font file not found: " + CUSTOM_FONTS.get(0)[1], 
            exception.getMessage()
        );
        assertEquals(
            "File not found!", 
            exception.getCause().getMessage()
        );
    }

    @Test
    void fsUriResolverReturnsExternalResourceUri() {
        var docBuilder = initDocBuilder(declareFonts(CUSTOM_FONTS));
        docBuilder.init(w3cDoc, mockOutputStream);
        var uriResolverCaptor = ArgumentCaptor.forClass(FSUriResolver.class);
        verify(mockPdfBuilder).useUriResolver(uriResolverCaptor.capture());
        uriResolverCaptor.getValue().resolveURI("/base", "file.ttf");
        verify(mockStreams).getExternalUrl("file.ttf");
    }

    @Test
    void setDataFileChangesTheDataFile() throws IOException {
        var docBuilder = initDocBuilder(declareFonts(CUSTOM_FONTS));
        var newMockDataFile = mock(File.class);
        docBuilder.setDataFile(newMockDataFile);
        docBuilder.makeW3CDoc(mock(InputStream.class), true);
        verify(mockDataParser).parseInputData(newMockDataFile);
    }

    @Test
    void setLocaleChangesTheLocale() {
        var docBuilder = initDocBuilder(declareFonts(CUSTOM_FONTS));
        var mockLocale = mock(Locale.class);
        docBuilder.setLocale(mockLocale);
        verify(mockDataPopulator).setLocale(mockLocale);
    }

    @Test
    void setFontChangesTheFont() {
        var docBuilder = initDocBuilder(declareFonts(DEFAULT_FONTS));
        docBuilder.setFont(declareFonts(CUSTOM_FONTS));
        docBuilder.init(w3cDoc, mockOutputStream);
        verifyUseFont(
            mockPdfBuilder, 
            mockStreams, 
            3, 
            CUSTOM_FONTS,
            (streams, fontPath) -> {
                try {
                    verify(streams).newFileInputStream(fontPath);
                } catch (FileNotFoundException e) { }
            }
        );
    }

    @Test
    void resetFontResetsFontToDefault() {
        var docBuilder = initDocBuilder(declareFonts(CUSTOM_FONTS));
        docBuilder.resetFont();
        docBuilder.init(w3cDoc, mockOutputStream);
        verifyUseFont(
            mockPdfBuilder, 
            mockStreams, 
            3, 
            DEFAULT_FONTS,
            (streams, fontPath) -> verify(streams).loadResource(fontPath)
        );
    }

}
