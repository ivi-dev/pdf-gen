package com.pdfgen;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.beust.jcommander.ParameterException;
import com.pdfgen.cli.DefaultArgParser;
import com.pdfgen.reporting.ConditionalI18NReporter;

class ProcTest {

    private String[] args = new String[] { "arg1=val1", "arg2=val2" };

    private DefaultArgParser<Args> argParser;

    private Args parsedArgs;
    
    private PDFGenerator pdfGenerator;
    
    private ConditionalI18NReporter reporter;
    
    private Proc proc;

    private void runProc(boolean verbose, Runnable verify) throws Exception {
        when(parsedArgs.getVerbose()).thenReturn(verbose);
        proc.run();
        verify.run();
    }

    private Exception makePdfGeneratorThrow(Class<Exception> excClass) throws Exception {
        var excMsg = "Generic error";
        var exc = mock(excClass);
        when(exc.getMessage()).thenReturn(excMsg);
        doThrow(exc).when(pdfGenerator).generate(anyBoolean());
        return exc;
    } 
    
    @SuppressWarnings("unchecked")
    @BeforeEach 
    void setUp() {
        argParser = mock(DefaultArgParser.class);
        parsedArgs = mock(Args.class);
        when(argParser.parse(args)).thenReturn(parsedArgs);
        pdfGenerator = mock(PDFGenerator.class);
        reporter = mock(ConditionalI18NReporter.class);
        proc = new Proc(
            args, 
            argParser, 
            (parsedArgs) -> pdfGenerator, 
            reporter
        );
    }

    @Test
    void minimalConstructorInitializesObject() {
        try (var mockedResourceBundle = mockStatic(ResourceBundle.class)) {
            mockedResourceBundle.when(
                () -> ResourceBundle.getBundle("i18n.Messages")
            ).thenReturn(mock(ResourceBundle.class));
            assertNotNull(new Proc(args));
        }
    }

    @Test
    void setArgsChangesInputArguments() throws Exception {
        var newArgs = new String[] { "newArg1=newVal1" };
        proc.setArgs(newArgs);
        runProc(false, () -> {
            verify(argParser).parse(newArgs);
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    void setArgParserChangesTheArgumentParser() throws Exception {
        var newArgParser = mock(DefaultArgParser.class);
        proc.setArgParser(newArgParser);
        runProc(false, () -> {
            verify(newArgParser).parse(args);
        });
    }

    @Test
    void setPdfGeneratorProviderChangesThePdfGeneratorProvider() throws Exception {
        var newPdfGenerator = mock(PDFGenerator.class);
        proc.setPdfGeneratorProvider((parsedArgs) -> newPdfGenerator);
        runProc(false, () -> {
            try {
                verify(newPdfGenerator).generate(false);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    @Test
    void setReporterChangesTheReporter() throws Exception {
        var newReporter = mock(ConditionalI18NReporter.class);
        proc.setReporter(newReporter);
        runProc(true, () -> {
            var inOrder = inOrder(newReporter);
            inOrder.verify(newReporter).setVerbose(true); 
            inOrder.verify(newReporter).info("finishedParsingArgs", parsedArgs);
            inOrder.verify(newReporter).success("documentGenerationSuccess");
        });
    }

    @Test
    void runCompletesSilently() throws Exception {
        runProc(false, () -> { 
            var inOrder = inOrder(reporter, pdfGenerator);
            inOrder.verify(reporter).setVerbose(false); 
            inOrder.verify(reporter).info("finishedParsingArgs", parsedArgs);
            try {
                inOrder.verify(pdfGenerator).generate(false);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            inOrder.verify(reporter).success("documentGenerationSuccess");
        });
    }

    @Test
    void runCompletesVerbosely() throws Exception {
        runProc(true, () -> {
             var inOrder = inOrder(reporter, pdfGenerator);
            inOrder.verify(reporter).setVerbose(true); 
            inOrder.verify(reporter).info("finishedParsingArgs", parsedArgs);
            try {
                inOrder.verify(pdfGenerator).generate(true);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            inOrder.verify(reporter).success("documentGenerationSuccess");
        });
    }

    @Test
    void runHandlesParameterException() throws Exception {
        String excMsg = "Parameter error";
        var paramEx = new ParameterException(excMsg);
        when(argParser.parse(args)).thenThrow(paramEx);
        runProc(false, () -> {
            var inOrder = inOrder(reporter, argParser);
            verify(reporter).setVerbose(true);
            inOrder.verify(reporter).error(
                "invalidCommandLineArgument", 
                excMsg
            );
            inOrder.verify(argParser).printUsage();
        });
    }

    @Test
    void runHandlesGeneralException() throws Exception {
        var exc = makePdfGeneratorThrow(Exception.class);
        runProc(false, () -> {
            verify(exc).printStackTrace();
        });
    }

}
