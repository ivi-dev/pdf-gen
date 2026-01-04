package com.pdfgen;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

class StandardResourceBundleWrapper implements ResourceBundleWrapper {

    private final ResourceBundle bundle;

    StandardResourceBundleWrapper(String baseName) {
        this.bundle = ResourceBundle.getBundle(baseName);
    }

    @Override
    public String getString(String key, Object ...args) {
        try {
            var val = bundle.getString(key);
            return args.length == 0 ? val : MessageFormat.format(val, args);
        } catch (NullPointerException | MissingResourceException e) {
            return key;
        }
    }

}