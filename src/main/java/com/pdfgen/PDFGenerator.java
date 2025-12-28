package com.pdfgen;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import com.pdfgen.converters.FontDeclaration;


// TODO: Implement custom exception handling...

class PDFGenerator {

    private String templatePath;

    private String outputFile;

    private final Streams streams;

    private final DocumentBuilder docBuilder;

    private final FileSystem fs;

    private static final String DEFAULT_OUTPUT_FILE = "Document.pdf";

    PDFGenerator(
        String templatePath, 
        File dataFile
    ) {
        this(
            templatePath, 
            dataFile, 
            DEFAULT_OUTPUT_FILE
        );
    }

    PDFGenerator(
        String templatePath, 
        File dataFile, 
        String outputFile
    ) {
        this(
            templatePath, 
            dataFile, 
            outputFile, 
            Locale.getDefault(),
            null
        );
    }

    PDFGenerator(
        String templatePath, 
        File dataFile, 
        String outputFile, 
        Locale locale,
        List<FontDeclaration> fonts
    ) {
        this(
            templatePath, 
            outputFile, 
            new DefaultStreams(),
            new DefaultDocumentBuilder(
                dataFile, 
                locale, 
                fonts
            ),
            new DefaultFileSystem()
        );
    }

    PDFGenerator(
        String templatePath, 
        String outputFile, 
        Streams streams,
        DocumentBuilder docBuilder,
        FileSystem fs
    ) {
        this.templatePath = templatePath;
        this.fs = fs;
        this.outputFile = resolveOutputFileName(outputFile);
        this.streams = streams;
        this.docBuilder = docBuilder;
    }

    private static String cleanOutputFileName(String path) {
        return !path.endsWith(".pdf") ? path + ".pdf" : path;
    }

    private String resolveOutputFileName(String filename) {
        if (filename == null) {
            return DEFAULT_OUTPUT_FILE;
        }
        var outputFilePath = fs.getPath(filename);
        return fs.isDirectory(outputFilePath) ? 
               outputFilePath.resolve(DEFAULT_OUTPUT_FILE).toString() : 
               cleanOutputFileName(filename);
    }

    void setTemplatePath(String value) {
        templatePath = value;
    }

    void setDataFile(File value) {
        docBuilder.setDataFile(value);
    }

    void setOutputFile(String value) {
        outputFile = resolveOutputFileName(value);
    }

    void setLocale(Locale value) {
        docBuilder.setLocale(value);
    }

    void setFont(List<FontDeclaration> value) {
        docBuilder.setFont(value);
    }

    void resetFont() {
        docBuilder.resetFont();
    }

    public void generate() throws Exception {
        try {
            try (var template = streams.newInputStream(Path.of(templatePath))) {
                var w3cDoc = docBuilder.makeW3CDoc(template);
                try (var out = streams.newFileOutputStream(outputFile)) {
                    var builder = docBuilder.init(w3cDoc, out);
                    builder.run();
                }
            }
        } catch  (Exception e) { 
            throw e;
        }
    }

}