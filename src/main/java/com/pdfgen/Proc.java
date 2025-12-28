package com.pdfgen;

import java.util.function.Function;

import com.beust.jcommander.ParameterException;
import com.pdfgen.cli.ArgParser;
import com.pdfgen.reporting.Reporter;

class Proc {

    private final Streams streams;

    Proc() { 
        this(new DefaultStreams());
    }

    Proc(Streams streams) { 
        this.streams = streams;
    }

     void run(
        String[] args, 
        ArgParser<Args> argParser, 
        Function<Args, PDFGenerator> pdfGeneratorProvider, 
        Reporter reporter
    ) {
        StandardStreams std = null;
        try {
            std = streams.muteStandardOuts();
            var parsedArgs = argParser.parse(args);
            pdfGeneratorProvider.apply(parsedArgs).generate();
            streams.unmuteStandardOuts(std);
            reporter.reportSuccess("PDF generated successfully!");
        } catch (Exception e) {
            handleException(e, reporter, argParser, streams, std);
        }
    }

    private static void handleException(
        Exception e, 
        Reporter reporter, 
        ArgParser<Args> argParser,
        Streams streams, 
        StandardStreams std
    ) {
        streams.unmuteStandardOuts(std);
        if (e instanceof ParameterException) {
            reporter.reportError(e.getMessage());
            argParser.printUsage();
            return;
        } 
        e.printStackTrace();
    }

}
