package com.pdfgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.pdfgen.converters.FontDeclaration;

public class PDFGeneratorTest {

    private Streams mockStreams;

    private DocumentBuilder mockDocBuilder;

    private PdfRendererBuilder mockPdfRendererBuilder;

    private FileSystem mockFs;

    private PDFGenerator pdfGenerator;

    @BeforeEach 
    void setUp() { 
        mockStreams = mock(Streams.class);
        mockDocBuilder = mock(DocumentBuilder.class);
        mockPdfRendererBuilder = mock(PdfRendererBuilder.class);
        when(mockDocBuilder.init(null, null)).thenReturn(mockPdfRendererBuilder);
        mockFs = mock(FileSystem.class);
        pdfGenerator = new PDFGenerator(
            "pdf-template.html", 
            "My-Document", 
            mockStreams, 
            mockDocBuilder,
            mockFs
        );
    }
    
    @Test
    void minimalConstructorInitializesObject() {
        var pdfGenerator = new PDFGenerator("pdf-template.html", mock(File.class));
        assertNotNull(pdfGenerator);
    }

    @Test
    void resolveOutputFileNameResolvesNullToDefaultOutputFilename() throws Exception {
        pdfGenerator.setOutputFile(null);
        pdfGenerator.generate(false);
        verify(mockStreams).newFileOutputStream("Document.pdf");
    }

    @Test
    void resolveOutputFileNameAddsFileExtension() throws Exception {
        pdfGenerator.setOutputFile("My-Document");
        pdfGenerator.generate(false);
        verify(mockStreams).newFileOutputStream("My-Document.pdf");
    }

    @Test
    void resolveOutputFileNameResolvesToFileInSpecifiedDirectory() throws Exception {
        var mockDirectory = "My-Directory";
        var mockOutputPath = Path.of(mockDirectory);
        when(mockFs.getPath(mockDirectory)).thenReturn(mockOutputPath);
        when(mockFs.isDirectory(mockOutputPath)).thenReturn(true);
        pdfGenerator.setOutputFile(mockDirectory);
        pdfGenerator.generate(false);
        verify(mockStreams).newFileOutputStream(
            Path.of(mockDirectory, "Document.pdf").toString()
        );
    }
    
    @Test
    void generateGeneratesPDFDocument() throws Exception {
        pdfGenerator.generate(false);
        verify(mockPdfRendererBuilder).run();
    }

    @Test
    void generateRethrowsExceptionThrownByGenerationProcess() throws Exception {
        var exceptionMessage = "IO exception!";
        doThrow(new IOException(exceptionMessage)).when(mockPdfRendererBuilder).run();
        var exception = assertThrows(Exception.class, () -> pdfGenerator.generate(false));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void setTemplatePathChangesTheTemplatePath() throws Exception {
        var mockTemplate = "New-Template.html";
        pdfGenerator.setTemplatePath(mockTemplate);
        pdfGenerator.generate(false);
        verify(mockStreams).newInputStream(Path.of(mockTemplate));
    }

    @Test
    void setDataFileChangesTheDataFile() throws Exception {
        var mockDataFile = mock(File.class);
        pdfGenerator.setDataFile(mockDataFile);
        verify(mockDocBuilder).setDataFile(mockDataFile);
    }

    @Test
    void setOutputFileChangesTheOutputFile() throws Exception {
        var mockOutputFile = "New-Document.pdf";
        pdfGenerator.setOutputFile(mockOutputFile);
        pdfGenerator.generate(false);
        verify(mockStreams).newFileOutputStream(mockOutputFile);
    }

    @Test
    void setLocaleChangesTheLocale() throws Exception {
        var mockLocale = mock(Locale.class);
        pdfGenerator.setLocale(mockLocale);
        verify(mockDocBuilder).setLocale(mockLocale);
    }

    @Test
    void setFontChangesTheFont() throws Exception {
        var mockFont = List.of(
            new FontDeclaration("Font-1", "/path/to/font-1.ttf")
        );
        pdfGenerator.setFont(mockFont);
        verify(mockDocBuilder).setFont(mockFont);
    }

    @Test
    void resetFontResetsFontToDefault() throws Exception {
        pdfGenerator.resetFont();
        verify(mockDocBuilder).resetFont();
    }

}
