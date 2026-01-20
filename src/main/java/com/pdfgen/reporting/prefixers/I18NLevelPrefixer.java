package com.pdfgen.reporting.prefixers;

import com.pdfgen.ResourceBundleWrapper;
import com.pdfgen.StandardResourceBundleWrapper;
import com.pdfgen.reporting.Level;

public class I18NLevelPrefixer implements Prefixer {

    private final ResourceBundleWrapper resBundleWrap;

    public I18NLevelPrefixer() {
        this(
            new StandardResourceBundleWrapper(
                "i18n.Messages"
            )
        );
    }

    public I18NLevelPrefixer(ResourceBundleWrapper resBundleWrap) {
        this.resBundleWrap = resBundleWrap;
    }

    @Override
    public String get(Level level) {
        return String.format(
            "%s:", 
            resBundleWrap.getString(level.name())
        );
    }

}