package com.pdfgen;

import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;

import com.beust.jcommander.ParameterException;
import com.openhtmltopdf.util.XRLog;
import com.pdfgen.cli.ArgParser;
import com.pdfgen.cli.DefaultArgParser;
import com.pdfgen.reporting.BasicReporter;
import com.pdfgen.reporting.ConditionalBasicReporter;
import com.pdfgen.reporting.ConditionalReporter;
import com.pdfgen.reporting.Reporters;

class Proc {

    private String[] args;

    private ArgParser<Args> argParser;

    private Args parsedArgs;

    private Function<Args, PDFGenerator> pdfGeneratorProvider;

    private Reporters reporters;

    private boolean verbose;

    private CheckedProcess pdfGeneratorProc = () -> {
        if (!verbose) disableLogging();
        pdfGeneratorProvider.apply(parsedArgs).generate(verbose);
    };

    private static final Function<Args, PDFGenerator> defaultPDFGeneratorProvider = 
        (parsedArgs) -> new PDFGenerator(
            parsedArgs.getTemplate(), 
            parsedArgs.getData(),
            parsedArgs.getOutput(), 
            parsedArgs.getLocale(),
            parsedArgs.getFont()
        );

    private static final Reporters defaultReporters = new Reporters(
        Map.of(
            "conditionalTimestampedPrefixedI18N",
            ConditionalBasicReporter.leveledTimestampedI18N(),
            "conditionalPrefixedI18N",
            ConditionalBasicReporter.leveledI18N(),
            "minimal",
            new BasicReporter()
        )
    );

    Proc(String[] args) { 
        this(
            args,
            new DefaultArgParser<>(new Args()), 
            defaultPDFGeneratorProvider,
            defaultReporters
        );
    }

    Proc(
        String[] args, 
        ArgParser<Args> argParser, 
        Function<Args, PDFGenerator> pdfGeneratorProvider, 
        Reporters reporters
    ) { 
        this.args = args;
        this.argParser = argParser;
        this.pdfGeneratorProvider = pdfGeneratorProvider;
        this.reporters = reporters;
    }

    private static void disableLogging() {
        XRLog.listRegisteredLoggers().forEach(logger ->
            XRLog.setLevel(logger, Level.OFF)
        );
    }

    void setArgs(String[] args) {
        this.args = args;
    }

    void setArgParser(ArgParser<Args> argParser) {
        this.argParser = argParser;
    }

    void setPdfGeneratorProvider(
        Function<Args, PDFGenerator> pdfGeneratorProvider
    ) {
        this.pdfGeneratorProvider = pdfGeneratorProvider;
    }

    void setReporters(Reporters reporters) {
        this.reporters = reporters;
    }

    private void setVerbose(boolean verbose) {
        reporters.invoke(
            "conditionalTimestampedPrefixedI18N", 
            (reporter) -> (
                (ConditionalReporter) reporter
            ).setVerbose(verbose)
        );
        reporters.invoke(
            "conditionalPrefixedI18N", 
            (reporter) -> (
                (ConditionalReporter) reporter
            ).setVerbose(verbose)
        );
    }

    private void printUsage() {
        setVerbose(true);
        reporters.info(
            "minimal", 
            argParser.getUsage()
        );
        setVerbose(false);
    }

    private void printVersion() {
        setVerbose(true);
        reporters.info(
            "minimal", 
            argParser.getMetaData()
        );
        setVerbose(false);
    }

     void run() {
        try {
            parsedArgs = argParser.parse(args);
            if (parsedArgs.getHelp()) {
                printUsage();
                return;
            }
            if (parsedArgs.getVersion()) {
                printVersion();
                return;
            }
            verbose = parsedArgs.getVerbose();
            setVerbose(verbose);
            reporters.info(
                "conditionalTimestampedPrefixedI18N", 
                "finishedParsingArgs", 
                parsedArgs
            );
            pdfGeneratorProc.run();
            reporters.success(
                "conditionalTimestampedPrefixedI18N",
                "documentGenerationSuccess"
            );
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        if (e instanceof ParameterException) {
            setVerbose(true);
            reporters.error(
                "conditionalPrefixedI18N",
                "invalidCommandLineArgument", 
                argParser.getUsage()
            );
            setVerbose(verbose);
            return;
        } 
        e.printStackTrace();
    }

}
