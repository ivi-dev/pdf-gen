package com.pdfgen;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.beust.jcommander.ParameterException;
import com.pdfgen.cli.DefaultArgParser;
import com.pdfgen.reporting.ConditionalReporter;
import com.pdfgen.reporting.Reporters;

class ProcTest {

    private String[] args = new String[] { "arg1=val1", "arg2=val2" };

    private DefaultArgParser<Args> argParser;

    private static final String usageInfo = "Program usage info.";

    private static final String versionInfo = "Program version info.";

    private Args parsedArgs;
    
    private PDFGenerator pdfGenerator;
    
    private Reporters reporters;
    
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
    private void assertSetVerbose(
        Reporters reporters, 
        InOrder inOrder, 
        boolean verbose
    ) {
        var procCaptor = ArgumentCaptor.forClass(Consumer.class);
        if (inOrder != null)
            inOrder.verify(reporters).invoke(
                eq("conditionalTimestampedI18N"), 
                procCaptor.capture()
            );
        else
            verify(reporters).invoke(
                eq("conditionalTimestampedI18N"), 
                procCaptor.capture()
            );
        var mockReporter = mock(ConditionalReporter.class);
        var proc = procCaptor.getValue();
        proc.accept(mockReporter);
        verify((ConditionalReporter)mockReporter).setVerbose(verbose);
    }

    private void assertPdfGeneratorCalled(InOrder inOrder, boolean verbose) {
        try {
            inOrder.verify(pdfGenerator).generate(verbose);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void assertProcRun(boolean verbose) {
        var inOrder = inOrder(reporters, pdfGenerator);
        assertSetVerbose(reporters, inOrder, verbose);
        inOrder.verify(reporters).info(
            "conditionalTimestampedI18N", 
            "finishedParsingArgs", 
            parsedArgs
        );
        assertPdfGeneratorCalled(inOrder, verbose);
        inOrder.verify(reporters).success(
            "conditionalTimestampedI18N",
            "documentGenerationSuccess"
        );
    }
    
    @SuppressWarnings("unchecked")
    @BeforeEach 
    void setUp() {
        argParser = mock(DefaultArgParser.class);
        when(argParser.getUsage()).thenReturn(usageInfo);
        when(argParser.getVersion()).thenReturn(versionInfo);
        parsedArgs = mock(Args.class);
        when(argParser.parse(args)).thenReturn(parsedArgs);
        pdfGenerator = mock(PDFGenerator.class);
        reporters = mock(Reporters.class);
        proc = new Proc(
            args, 
            argParser, 
            (parsedArgs) -> pdfGenerator, 
            reporters
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
        var newParsedArgs = mock(Args.class);
        when(newParsedArgs.getVerbose()).thenReturn(false);
        when(argParser.parse(newArgs)).thenReturn(newParsedArgs);
        runProc(false, () -> {
            verify(argParser).parse(newArgs);
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    void setArgParserChangesTheArgumentParser() throws Exception {
        var newArgParser = mock(DefaultArgParser.class);
        var newParsedArgs = mock(Args.class);
        when(newParsedArgs.getVerbose()).thenReturn(false);
        when(newArgParser.parse(args)).thenReturn(newParsedArgs);
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
    void setReportersChangesTheReporter() throws Exception {
        reporters = mock(Reporters.class);
        proc.setReporters(reporters);
        var verbose = true;
        runProc(verbose, () -> {
            assertProcRun(verbose);
        });
    }

    @Test
    void runPrintsHelpInfo() throws Exception {
        when(parsedArgs.getHelp()).thenReturn(true);
        runProc(false, () -> { 
            var inOrder = inOrder(reporters);
            assertSetVerbose(reporters, inOrder, true);
            inOrder.verify(reporters).info("minimal", usageInfo);
            assertSetVerbose(reporters, inOrder, false);
        });
    }

    @Test
    void runPrintsVersionInfo() throws Exception {
        when(parsedArgs.getVersion()).thenReturn(true);
        runProc(false, () -> { 
            var inOrder = inOrder(reporters);
            assertSetVerbose(reporters, inOrder, true);
            inOrder.verify(reporters).info("minimal", versionInfo);
            assertSetVerbose(reporters, inOrder, false);
        });
    }

    @Test
    void runCompletesSilently() throws Exception {
        var verbose = false;
        runProc(verbose, () -> { 
            assertProcRun(verbose);
        });
    }

    @Test
    void runCompletesVerbosely() throws Exception {
        var verbose = true;
        runProc(verbose, () -> {
            assertProcRun(verbose);
        });
    }

    @Test
    void runHandlesParameterException() throws Exception {
        String excMsg = "Parameter error";
        var paramEx = new ParameterException(excMsg);
        when(argParser.parse(args)).thenThrow(paramEx);
        var verbose = false;
        runProc(verbose, () -> {
            var inOrder = inOrder(reporters, pdfGenerator);
            assertSetVerbose(reporters, inOrder, true);
            inOrder.verify(reporters).error(
                "conditionalTimestampedI18N",
                "invalidCommandLineArgument", 
                usageInfo
            );
            assertSetVerbose(reporters, inOrder, verbose);
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
