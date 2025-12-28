package com.pdfgen;

import java.util.function.Function;

import com.beust.jcommander.ParameterException;
import com.pdfgen.cli.ArgParser;
import com.pdfgen.reporting.Reporter;

class Proc {

    private Proc() { }

    static void run(
        String[] args, 
        ArgParser<Args> argParser, 
        Function<Args, PDFGenerator> pdfGeneratorFactory, 
        Reporter reporter
    ) {
        try {
            var parsedArgs = argParser.parse(args);
            pdfGeneratorFactory.apply(parsedArgs).generate();
            reporter.reportSuccess("PDF generated successfully!");
        } catch (ParameterException e) {
            reporter.reportError(e.getMessage());
            argParser.printUsage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
