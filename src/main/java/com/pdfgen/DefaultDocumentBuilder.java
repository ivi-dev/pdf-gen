package com.pdfgen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.FSUriResolver;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import com.pdfgen.converters.FontDeclaration;
import com.pdfgen.reporting.ConditionalReporter;
import com.pdfgen.reporting.StandardConditionalTimestampedI18NReporter;

class DefaultDocumentBuilder implements DocumentBuilder {

    private File dataFile;

    private Map<String, Object> data;

    private DataParser dataParser;

    private DataPopulator dataPopulator;

    private ConditionalReporter reporter;

    private List<FontDeclaration> fonts;    

    private boolean fontsAreCustom = false;

    private final Streams streams;

    private final PdfRendererBuilder pdfBuilder;

    private final Function<FontDeclaration, FSSupplier<InputStream>> fontFileSupplier;

    private final FSUriResolver fsUriResolver;

    private static final List<FontDeclaration> DEFAULT_FONTS = List.of(
        new FontDeclaration("Light", "/fonts/NotoSans-Light.ttf"),
        new FontDeclaration("Regular", "/fonts/NotoSans-Regular.ttf"),
        new FontDeclaration("Medium", "/fonts/NotoSans-Medium.ttf")
    );

    DefaultDocumentBuilder(File dataFile, Locale locale, List<FontDeclaration> fonts) {
        this(
            dataFile, 
            fonts, 
            new DefaultStreams(),
            new PdfRendererBuilder(),
            new DefaultDataParser(),
            new DefaultDataPopulator(locale),
            new StandardConditionalTimestampedI18NReporter(
                new StandardResourceBundleWrapper(
                    "i18n.Messages"
                )
            )
        );
    }

    DefaultDocumentBuilder(
        File dataFile, 
        List<FontDeclaration> fonts, 
        Streams streams,
        PdfRendererBuilder pdfBuilder,
        DataParser dataParser,
        DataPopulator dataPopulator,
        ConditionalReporter reporter
    ) {
        this.dataFile = dataFile;
        this.fonts = resolveFonts(fonts);
        this.streams = streams;
        this.pdfBuilder = pdfBuilder;
        this.dataParser = dataParser;
        this.dataPopulator = dataPopulator;
        fontFileSupplier = (font) -> {
            return () -> {
                try {
                    return this.streams.newFileInputStream(font.path());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Font file not found: " + font.path(), e);
                }
            };
        };
        fsUriResolver = (baseUri, uri) -> this.streams.getExternalUrl(uri);
        this.reporter = reporter;
    }

    private List<FontDeclaration> resolveFonts(List<FontDeclaration> fonts) {
        var fonts_ = fonts == null ? DEFAULT_FONTS : fonts;
        fontsAreCustom = fonts != null;
        return fonts_;
    }

    @Override
    public void setDataFile(File value) {
        dataFile = value;
    }

    @Override
    public void setLocale(Locale value) {
        dataPopulator.setLocale(value);
    }

    @Override
    public void setFont(List<FontDeclaration> value) {
        fonts = value;
        fontsAreCustom = true;
    }

    @Override
    public void resetFont() {
        fonts = DEFAULT_FONTS;
        fontsAreCustom = false;
    }

    @Override
    public org.w3c.dom.Document makeW3CDoc(
        InputStream template, 
        boolean verbose
    ) throws IOException {
        reporter.setVerbose(verbose);
        reporter.info("parsingTemplate");
        var dom = dataParser.parseTemplate(template);
        reporter.info("populatingDate");
        dataPopulator.populateGenerationDate(dom);
        reporter.info("parsingInputData", dataFile.getAbsolutePath());
        data = dataParser.parseInputData(dataFile);
        reporter.info("populatingInputData");
        dataPopulator.populateData(dom, data, null);
        return dataParser.makeW3CDoc(dom);
    }

    private void loadFonts(PdfRendererBuilder builder) {
        if (!fontsAreCustom) {
            fonts.forEach((font) -> {
                builder.useFont(
                    () -> streams.loadResource(font.path()), 
                    font.name()
                );
            });
        } else {
            fonts.forEach((font) -> 
                builder.useFont(
                    fontFileSupplier.apply(font), 
                    font.name()
                )
            );
        }
    } 

    @Override
    public PdfRendererBuilder init(org.w3c.dom.Document w3cDoc, FileOutputStream os) {
        pdfBuilder.useFastMode();
        pdfBuilder.useUriResolver(fsUriResolver);
        pdfBuilder.withW3cDocument(w3cDoc, "");
        pdfBuilder.toStream(os);
        pdfBuilder.useSVGDrawer(new BatikSVGDrawer());
        loadFonts(pdfBuilder);
        return pdfBuilder;
    }

}