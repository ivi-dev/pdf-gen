package com.pdfgen;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import com.pdfgen.converters.FontDeclaration;
import com.pdfgen.reporting.ConditionalReporter;
import com.pdfgen.reporting.StandardConditionalTimestampedI18NReporter;

class PDFGenerator {

    private String templatePath;

    private String outputFile;

    private final Streams streams;

    private final DocumentBuilder docBuilder;

    private final FileSystem fs;

    private ConditionalReporter reporter;

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
            new DefaultFileSystem(),
            new StandardConditionalTimestampedI18NReporter(
                new StandardResourceBundleWrapper(
                    "i18n.Messages"
                )
            )
        );
    }

    PDFGenerator(
        String templatePath, 
        String outputFile, 
        Streams streams,
        DocumentBuilder docBuilder,
        FileSystem fs,
        ConditionalReporter reporter
    ) {
        this.templatePath = templatePath;
        this.fs = fs;
        this.outputFile = resolveOutputFileName(outputFile);
        this.streams = streams;
        this.docBuilder = docBuilder;
        this.reporter = reporter;
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

    public void generate(boolean verbose) throws Exception {
        reporter.setVerbose(verbose);
        try {
            reporter.info("readingDocumentTemplate", templatePath);
            try (var template = streams.newInputStream(Path.of(templatePath))) {
                var w3cDoc = docBuilder.makeW3CDoc(template, verbose);
                try (var out = streams.newFileOutputStream(outputFile)) {
                    reporter.info("startDocumentGeneration", outputFile);
                    docBuilder.init(w3cDoc, out).run();
                }
            }
        } catch  (Exception e) { 
            throw e;
        }
    }

}