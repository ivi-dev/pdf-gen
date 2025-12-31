package com.pdfgen;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

public class StandardResourceBundleWrapperTest {

    @Test
    void getStringReturnsPlainString() {
        try (var mockedResourceBundle = mockStatic(ResourceBundle.class)) {
            var mockResourceBundle = mock(ResourceBundle.class);
            when(mockResourceBundle.getString("plainString")).thenReturn("A plain string.");
            mockedResourceBundle.when(
                () -> ResourceBundle.getBundle("i18n.Messages")
            ).thenReturn(mockResourceBundle);
            var wrapper = new StandardResourceBundleWrapper("i18n.Messages");
            var result = wrapper.getString("plainString");
            assert result.equals("A plain string.");
        }
    }

    @Test
    void getStringReturnsFormattedString() {
        try (var mockedResourceBundle = mockStatic(ResourceBundle.class)) {
            var mockResourceBundle = mock(ResourceBundle.class);
            when(mockResourceBundle.getString("formattedString")).thenReturn("Hello, {0}!");
            mockedResourceBundle.when(
                () -> ResourceBundle.getBundle("i18n.Messages")
            ).thenReturn(mockResourceBundle);
            var wrapper = new StandardResourceBundleWrapper("i18n.Messages");
            var result = wrapper.getString("formattedString", "World");
            assert result.equals("Hello, World!");
        }
    }

}
