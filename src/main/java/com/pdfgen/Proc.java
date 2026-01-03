package com.pdfgen;

import java.util.function.Function;
import java.util.logging.Level;

import com.beust.jcommander.ParameterException;
import com.openhtmltopdf.util.XRLog;
import com.pdfgen.cli.ArgParser;
import com.pdfgen.cli.DefaultArgParser;
import com.pdfgen.reporting.ConditionalI18NReporter;
import com.pdfgen.reporting.MinimalReporter;
import com.pdfgen.reporting.StandardConditionalI18NReporter;

class Proc {

    private String[] args;

    private ArgParser<Args> argParser;

    private Args parsedArgs;

    private Function<Args, PDFGenerator> pdfGeneratorProvider;

    private ConditionalI18NReporter reporter;

    private boolean verbose;

    private CheckedProcess pdfGeneratorProc = () -> {
        disableLogging();
        pdfGeneratorProvider.apply(parsedArgs).generate(verbose);
    };

    Proc(String[] args) { 
        this(
            args,
            new DefaultArgParser<>(new Args()), 
            (parsedArgs) -> 
                new PDFGenerator(
                    parsedArgs.getTemplate(), 
                    parsedArgs.getData(),
                    parsedArgs.getOutput(), 
                    parsedArgs.getLocale(),
                    parsedArgs.getFont()
                ),
            new StandardConditionalI18NReporter(
                new StandardResourceBundleWrapper(
                    "i18n.Messages"
                )
            )
        );
    }

    Proc(
        String[] args, 
        ArgParser<Args> argParser, 
        Function<Args, PDFGenerator> pdfGeneratorProvider, 
        ConditionalI18NReporter reporter
    ) { 
        this.args = args;
        this.argParser = argParser;
        this.pdfGeneratorProvider = pdfGeneratorProvider;
        this.reporter = reporter;
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

    void setReporter(ConditionalI18NReporter reporter) {
        this.reporter = reporter;
    }

    private void printHelp() {
        reporter.setVerbose(true);
        new MinimalReporter().info(argParser.getUsage()); // ?
        reporter.setVerbose(false);
    }

     void run() {
        try {
            parsedArgs = argParser.parse(args);
            if (parsedArgs.getHelp()) {
                printHelp();
                return;
            }
            verbose = parsedArgs.getVerbose();
            reporter.setVerbose(verbose);
            reporter.info("finishedParsingArgs", parsedArgs);
            pdfGeneratorProc.run();
            reporter.success("documentGenerationSuccess");
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        if (e instanceof ParameterException) {
            reporter.setVerbose(true);
            reporter.error(
                "invalidCommandLineArgument", 
                argParser.getUsage()
            );
            return;
        } 
        e.printStackTrace();
    }

}
