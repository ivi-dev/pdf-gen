package com.pdfgen.reporting;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.pdfgen.reporting.modifiers.MessageModifier;
import com.pdfgen.reporting.prefixers.Prefixer;

public class ConditionalBasicReporterTest 
    extends AbstractReporterTest<ConditionalBasicReporter> {

    private static final String I18N_MSG = "I18N";
    
    private static final String I18N_INFO = "I18N-INFO";
    
    private static final String I18N_ERROR = "I18N-ERROR";

    private static final String I18_SUCCESS = "I18N-SUCCESS";

    private void mockResourceBundle(MockedStatic<ResourceBundle> target) {
        var resBundle = mock(ResourceBundle.class);
        when(resBundle.getString("INFO")).thenReturn(I18N_INFO);
        when(resBundle.getString(INFO_KEY)).thenReturn(I18N_MSG);
        when(resBundle.getString("ERROR")).thenReturn(I18N_ERROR);
        when(resBundle.getString(ERROR_KEY)).thenReturn(I18N_MSG);
        when(resBundle.getString("SUCCESS")).thenReturn(I18_SUCCESS);
        when(resBundle.getString(SUCCESS_KEY)).thenReturn(I18N_MSG);
        target.when(
            () -> ResourceBundle.getBundle(anyString())
        ).thenReturn(resBundle);
    }

    @Override
    protected ConditionalBasicReporter getReporter() {
        return new ConditionalBasicReporter();
    }

    @Test
    void constructorInitializesObjectWithVerbosityFalse() {
        assertLoggingSilent();
    }

    @Test
    void constructorInitializesObjectWithVerbosityTrue() {
        reporter = new ConditionalBasicReporter(true);
        assertPlainLogging();
    }

    @Test
    void constructorInitializesObjectWithPrefixersAndVerbosityFalse() {
        var prefixer = mock(Prefixer.class);
        var prefix = "prefix";
        when(prefixer.get(any())).thenReturn(prefix);
        reporter = new ConditionalBasicReporter(new Prefixer[] { prefixer });
        assertLoggingSilent();
    }

    @Test
    void constructorInitializesObjectWithPrefixersAndVerbosityTrue() {
        var prefixer = mock(Prefixer.class);
        var prefix = "prefix";
        when(prefixer.get(any())).thenReturn(prefix);
        reporter = new ConditionalBasicReporter(true, new Prefixer[] { prefixer });
        assertPrefixedLogging(prefix);
    }

    @Test
    void constructorInitializesObjectWithModifiersAndVerbosityFalse() {
        var modifier = mock(MessageModifier.class);
        var modified = "modified";
        when(modifier.modify(anyString())).thenReturn(modified);
        reporter = new ConditionalBasicReporter(new MessageModifier[] { modifier });
        assertLoggingSilent();
    }

    @Test
    void constructorInitializesObjectWithModifiersAndVerbosityTrue() {
        var modifier = mock(MessageModifier.class);
        var modified = "modified";
        when(modifier.modify(anyString())).thenReturn(modified);
        reporter = new ConditionalBasicReporter(true, new MessageModifier[] { modifier });
        assertModifiedLogging(modified);
    }

    @Test
    void constructorInitializesObjectWithPrefixersAndModifiersAndVerbosityFalse() {
        var prefixer = mock(Prefixer.class);
        var prefix = "prefix";
        when(prefixer.get(any())).thenReturn(prefix);
        var modifier = mock(MessageModifier.class);
        var modified = "modified";
        when(modifier.modify(anyString())).thenReturn(modified);
        reporter = new ConditionalBasicReporter(
            new Prefixer[] { prefixer },
            new MessageModifier[] { modifier }
        );
        assertLoggingSilent();
    }

    @Test
    void constructorInitializesObjectWithPrefixersAndModifiersAndVerbosityTrue() {
        var prefixer = mock(Prefixer.class);
        var prefix = "prefix";
        when(prefixer.get(any())).thenReturn(prefix);
        var modifier = mock(MessageModifier.class);
        var modified = "modified";
        when(modifier.modify(anyString())).thenReturn(modified);
        reporter = new ConditionalBasicReporter(
            true, 
            new Prefixer[] { prefixer },
            new MessageModifier[] { modifier }
        );
        assertPrefixedModifiedLogging(prefix, modified);
    }

    @Test
    void infoWritesToStandardErr() {
        reporter.setVerbose(true);
        reporter.info(INFO_KEY);
        assertMessageLogged(INFO_KEY);
    }

    @Test
    void errorWritesToStandardErr() {
        reporter.setVerbose(true);
        reporter.error(ERROR_KEY);
        assertMessageLogged(ERROR_KEY);
    }

    @Test
    void successWritesToStandardErr() {
        reporter.setVerbose(true);
        reporter.success(SUCCESS_KEY);
        assertMessageLogged(SUCCESS_KEY);
    }

    @Test
    void setVerboseChangesTheVerbosity() {
        assertLoggingSilent();
        reporter.setVerbose(true);
        assertPlainLogging();
    }

    @Test
    void leveledTimestampedI18NReturnsCorrectlyConfiguredReporter() {
        try (var mockedResBundle = mockStatic(ResourceBundle.class)) {
            mockResourceBundle(mockedResBundle);
            try (var mockedDatetime = mockStatic(LocalDateTime.class)) {
                var formattedDate = "NOW";
                var mockNow = mock(LocalDateTime.class);
                when(mockNow.format(any())).thenReturn(formattedDate);
                mockedDatetime.when(LocalDateTime::now).thenReturn(mockNow);
                var reporter = ConditionalBasicReporter.leveledTimestampedI18N();
                reporter.setVerbose(true);
                reporter.info(INFO_KEY);
                assertMessageLoggedWithFormat("%s: [%s] %s", I18N_INFO, "NOW", I18N_MSG);
                reporter.error(ERROR_KEY);
                assertMessageLoggedWithFormat("%s: [%s] %s", I18N_ERROR, "NOW", I18N_MSG);
                reporter.success(SUCCESS_KEY);
                assertMessageLoggedWithFormat("%s: [%s] %s", I18_SUCCESS, "NOW", I18N_MSG);
            }
        }
    }

    @Test
    void leveledI18NReturnsCorrectlyConfiguredReporter() {
        try (var mockedResBundle = mockStatic(ResourceBundle.class)) {
            mockResourceBundle(mockedResBundle);
            var reporter = ConditionalBasicReporter.leveledI18N();
            reporter.setVerbose(true);
            reporter.info(INFO_KEY);
            assertMessageLoggedWithFormat("%s: %s", I18N_INFO, I18N_MSG);
            reporter.error(ERROR_KEY);
            assertMessageLoggedWithFormat("%s: %s", I18N_ERROR, I18N_MSG);
            reporter.success(SUCCESS_KEY);
            assertMessageLoggedWithFormat("%s: %s", I18_SUCCESS, I18N_MSG);
        }
    }

}
