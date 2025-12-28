package com.pdfgen;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.beust.jcommander.ParameterException;
import com.pdfgen.cli.DefaultArgParser;
import com.pdfgen.reporting.StandardOutReporter;

class ProcTest {

    private DefaultArgParser<Args> argParser;

    private PDFGenerator energyReport;
    
    private StandardOutReporter reporter;
    
    @SuppressWarnings("unchecked")
    @BeforeEach 
    void setUp() {
        argParser = mock(DefaultArgParser.class);
        energyReport = mock(PDFGenerator.class);
        reporter = mock(StandardOutReporter.class);
    }

    @Test
    void runCompletesWithSuccessMessage() {
        Proc.run(new String[] { }, argParser, (parsedArgs) -> energyReport, reporter);
        verify(reporter).reportSuccess("PDF generated successfully!");
    }

    @Test
    void runHandlesParameterException() {
        var excMsg = "Wrong parameter detected in the command line!";
        doThrow(new ParameterException(excMsg))
            .when(argParser)
            .parse(new String[] { "wrong-arg" });
        Proc.run(new String[] { "wrong-arg" }, argParser, (parsedArgs) -> energyReport, reporter);
        verify(reporter).reportError(excMsg);
        verify(argParser).printUsage();
    }

    @Test
    void runHandlesGenericException() throws Exception {
        var exception = mock(Exception.class);
        doThrow(exception).when(energyReport).generate();
        Proc.run(new String[] { }, argParser, (parsedArgs) -> energyReport, reporter);
        verify(exception).printStackTrace();
    }

}
