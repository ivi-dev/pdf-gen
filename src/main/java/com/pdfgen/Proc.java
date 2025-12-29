package com.pdfgen;

import java.io.PrintStream;
import java.util.function.Function;
import java.util.function.Supplier;

import com.beust.jcommander.ParameterException;
import com.pdfgen.cli.ArgParser;
import com.pdfgen.cli.DefaultArgParser;
import com.pdfgen.reporting.Reporter;
import com.pdfgen.reporting.StandardReporter;

@FunctionalInterface
interface CheckedProcess {

    void run() throws Exception;

}

class Proc {

    private String[] args;

    private ArgParser<Args> argParser;

    private Args parsedArgs;

    private Function<Args, PDFGenerator> pdfGeneratorProvider;

    private Reporter reporter;

    private boolean verbose;

    private CheckedProcess pdfGeneratorProc = () -> {
        pdfGeneratorProvider.apply(parsedArgs).generate(verbose);
    };

    private final Streams streams;

    private PrintStream origOut;

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
            new StandardReporter(),
            new DefaultStreams()
        );
    }

    Proc(
        String[] args, 
        ArgParser<Args> argParser, 
        Function<Args, PDFGenerator> pdfGeneratorProvider, 
        Reporter reporter,
        Streams streams
    ) { 
        this.args = args;
        this.argParser = argParser;
        this.pdfGeneratorProvider = pdfGeneratorProvider;
        this.reporter = reporter;
        this.streams = streams;
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
            origOut = run(() -> streams.muteStandardOut(), Messages.muteOutput());
            tryInfo(Messages.parseArgs(args, " "));
            runVoid(pdfGeneratorProc, Messages.startPdfGeneration());
            runVoid(() -> streams.unmuteStandardOut(origOut), Messages.unmuteOutput());
            trySuccess(Messages.success());
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        if (origOut != null)
            streams.unmuteStandardOut(origOut);
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
        
        private static final String MUTE_OUTPUT = "Muting standard output temporarily to prevent " +
                                                  "external-library-generated logging \"noise\".";

        private static final String START_PDF_GENERATION = "Starting PDF document generation.";

        private static final String UNMUTE_OUTPUT = "Unmuting standard output. It can now be written to again.";

        private static final String SUCCESS = "PDF generated successfully!";

        private static final String GENERIC_ERROR = "ERROR: Could not generate PDF. Reason: %s.";

        static String parseArgs(String[] args, String delim) {
            return String.format(PARSE_ARGS, String.join(delim, args));
        }

        static String muteOutput() {
            return MUTE_OUTPUT;
        }

        static String startPdfGeneration() {
            return START_PDF_GENERATION;
        }

        static String unmuteOutput() {
            return UNMUTE_OUTPUT;
        }

        static String success() {
            return SUCCESS;
        }

        static String error(Exception e) {
            return String.format(GENERIC_ERROR, e.getMessage());
        }

    }

}
