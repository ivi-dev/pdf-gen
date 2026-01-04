package com.pdfgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
            var resName = "i18n.Messages";
            var key = "plainString";
            var result = "A plain string.";
            when(mockResourceBundle.getString(key)).thenReturn(result);
            mockedResourceBundle.when(
                () -> ResourceBundle.getBundle(resName)
            ).thenReturn(mockResourceBundle);
            var wrapper = new StandardResourceBundleWrapper(resName);
            assertEquals(wrapper.getString(key), result);
        }
    }

    @Test
    void getStringReturnsFormattedString() {
        try (var mockedResourceBundle = mockStatic(ResourceBundle.class)) {
            var mockResourceBundle = mock(ResourceBundle.class);
            var resName = "i18n.Messages";
            var key = "formattedString";
            when(mockResourceBundle.getString(key)).thenReturn("Hello, {0}!");
            mockedResourceBundle.when(
                () -> ResourceBundle.getBundle(resName)
            ).thenReturn(mockResourceBundle);
            var wrapper = new StandardResourceBundleWrapper(resName);
            var result = wrapper.getString(key, "World");
            assertEquals(result, "Hello, World!");
        }
    }

    @Test
    void getStringReturnsNullIfProvidedKeyisNull() {
        try (var mockedResourceBundle = mockStatic(ResourceBundle.class)) {
            var mockResourceBundle = mock(ResourceBundle.class);
            var resName = "i18n.Messages";
            when(
                mockResourceBundle.getString(null)
            ).thenThrow(new NullPointerException());
            mockedResourceBundle.when(
                () -> ResourceBundle.getBundle(resName)
            ).thenReturn(mockResourceBundle);
            var wrapper = new StandardResourceBundleWrapper(resName);
            var result = wrapper.getString(null, "World");
            assertEquals(result, null);
        }
    }

    @Test
    void getStringReturnsTheProvidedKeyIfSearchedResourceIsMissing() {
        try (var mockedResourceBundle = mockStatic(ResourceBundle.class)) {
            var mockResourceBundle = mock(ResourceBundle.class);
            var resName = "i18n.Messages";
            var key = "missing-resource";
            when(
                mockResourceBundle.getString(key)
            ).thenThrow(new NullPointerException());
            mockedResourceBundle.when(
                () -> ResourceBundle.getBundle(resName)
            ).thenReturn(mockResourceBundle);
            var wrapper = new StandardResourceBundleWrapper(resName);
            var result = wrapper.getString(key);
            assertEquals(result, key);
        }
    }

}
