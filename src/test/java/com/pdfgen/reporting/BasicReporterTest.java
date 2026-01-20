package com.pdfgen.reporting;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.pdfgen.reporting.modifiers.MessageModifier;
import com.pdfgen.reporting.prefixers.Prefixer;

public class BasicReporterTest extends AbstractReporterTest<BasicReporter> {

    @Override
    protected BasicReporter getReporter() {
        return new BasicReporter();
    }

    @Test
    void constructorInitializesObjectWithNoPrefixersOrModifiers() {
        assertPlainLogging();
    }

    @Test
    void constructorInitializesObjectWithPrefixers() {
        var prefixer = mock(Prefixer.class);
        var prefix = "prefix";
        when(prefixer.get(any())).thenReturn(prefix);
        reporter = new BasicReporter(new Prefixer[] { prefixer });
        assertPrefixedLogging(prefix);
    }

    @Test
    void constructorInitializesObjectWithModifiers() {
        var modifier = mock(MessageModifier.class);
        var modified = "modified";
        when(modifier.modify(anyString())).thenReturn(modified);
        reporter = new BasicReporter(new MessageModifier[] { modifier });
        assertModifiedLogging(modified);
    }

    @Test
    void constructorInitializesObjectWithPrefixersAndModifiers() {
        var prefixer = mock(Prefixer.class);
        var prefix = "prefix";
        when(prefixer.get(any())).thenReturn(prefix);
        var modifier = mock(MessageModifier.class);
        var modified = "modified";
        when(modifier.modify(anyString())).thenReturn(modified);
        reporter = new BasicReporter(
            new Prefixer[] { prefixer }, 
            new MessageModifier[] { modifier }
        );
        assertPrefixedModifiedLogging(prefix, modified);
    }

    @Test
    void infoWritesToStandardErr() {
        reporter.info(INFO_KEY);
        assertMessageLogged(INFO_KEY);
    }

    @Test
    void errorWritesToStandardErr() {
        reporter.error(ERROR_KEY);
        assertMessageLogged(ERROR_KEY);
    }

    @Test
    void successWritesToStandardErr() {
        reporter.success(SUCCESS_KEY);
        assertMessageLogged(SUCCESS_KEY);
    }

}
