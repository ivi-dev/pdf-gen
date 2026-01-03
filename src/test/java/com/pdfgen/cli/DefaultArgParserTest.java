package com.pdfgen.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import com.beust.jcommander.Parameter;

class MockArgs {

    @Parameter(names = {"--arg1"}, description = "First argument")
    private String arg1;

    @Parameter(names = {"--arg2"}, description = "Second argument")
    private String arg2;

    String getArg1() {
        return arg1;
    }

    String getArg2() {
        return arg2;
    }

}

public class DefaultArgParserTest {

    @Test
    void parseReturnsCorrectArgumentValues() {
        var args = new MockArgs();
        var argParser = new DefaultArgParser<>(args);
        argParser.parse(new String[] { "--arg1", "val1", "--arg2=val2" });
        assertEquals("val1", args.getArg1());
        assertEquals("val2", args.getArg2());
    }

    @Test
    void getUsageReturnsAppUsageInfo() {
        var args = new MockArgs();
        var appEngine = mock(AppEngine.class);
        @SuppressWarnings("unchecked")
        var argParser = new DefaultArgParser<MockArgs>(args, appEngine);
        argParser.getUsage();
        verify(appEngine).getUsage();
    }

}
