package com.pdfgen;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

import com.beust.jcommander.ParameterException;
import com.openhtmltopdf.util.XRLog;
import com.pdfgen.cli.ArgParser;
import com.pdfgen.cli.DefaultArgParser;
import com.pdfgen.reporting.Reporter;
import com.pdfgen.reporting.StandardReporter;

class Proc {

    private String[] args;

    private ArgParser<Args> argParser;

    private Args parsedArgs;

    private Function<Args, PDFGenerator> pdfGeneratorProvider;

    private Reporter reporter;

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
            new StandardReporter()
        );
    }

    Proc(
        String[] args, 
        ArgParser<Args> argParser, 
        Function<Args, PDFGenerator> pdfGeneratorProvider, 
        Reporter reporter
    ) { 
        this.args = args;
        this.argParser = argParser;
        this.pdfGeneratorProvider = pdfGeneratorProvider;
        this.reporter = reporter;
    }

    void setArgs(String[] args) {
        this.args = args;
    }

    void setArgParser(ArgParser<Args> argParser) {
        this.argParser = argParser;
    }

    void setPdfGeneratorProvider(Function<Args, PDFGenerator> pdfGeneratorProvider) {
        this.pdfGeneratorProvider = pdfGeneratorProvider;
    }

    void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    private static void disableLogging() {
        XRLog.listRegisteredLoggers().forEach(logger ->
            XRLog.setLevel(logger, Level.OFF)
        );
    }

    private <T> T run(Supplier<T> proc, String msg) {
        tryInfo(msg);
        return proc.get();
    }

    private void runVoid(CheckedProcess proc, String msg) throws Exception{
        tryInfo(msg);
        proc.run();
    }

    private void trySuccess(String msg) {
        if (verbose)
            reporter.success(msg);
    }

    private void tryInfo(String msg) {
        if (verbose)
            reporter.info(msg);
    }

     void run() {
        try {
            parsedArgs = run(() -> argParser.parse(args), Messages.parseArgs(args, " "));
            verbose = parsedArgs.getVerbose();
            tryInfo(Messages.parseArgs(args, " "));
            runVoid(pdfGeneratorProc, Messages.startPdfGeneration());
            trySuccess(Messages.success());
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        if (e instanceof ParameterException) {
            reporter.error(e.getMessage());
            argParser.printUsage();
            return;
        } 
        if (verbose)
            e.printStackTrace();
        else
            reporter.error(Messages.error(e));
    }

    private static class Messages {

        private static final String PARSE_ARGS = "Parsed command-line argments: %s.";
        
        private static final String START_PDF_GENERATION = "Starting PDF document generation.";

        private static final String SUCCESS = "PDF generated successfully!";

        private static final String GENERIC_ERROR = "ERROR: Could not generate PDF. Reason: %s.";

        static String parseArgs(String[] args, String delim) {
            return String.format(PARSE_ARGS, String.join(delim, args));
        }

        static String startPdfGeneration() {
            return START_PDF_GENERATION;
        }

        static String success() {
            return SUCCESS;
        }

        static String error(Exception e) {
            return String.format(GENERIC_ERROR, e.getMessage());
        }

    }

}
