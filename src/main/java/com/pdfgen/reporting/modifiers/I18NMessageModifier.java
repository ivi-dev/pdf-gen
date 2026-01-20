package com.pdfgen.reporting.modifiers;

import com.pdfgen.ResourceBundleWrapper;
import com.pdfgen.StandardResourceBundleWrapper;

public class I18NMessageModifier implements MessageModifier {

    private final ResourceBundleWrapper resBundleWrap;

    public I18NMessageModifier() {
        this(
            new StandardResourceBundleWrapper(
                "i18n.Messages"
            )
        );
    }

    public I18NMessageModifier(ResourceBundleWrapper resBundleWrap) {
        this.resBundleWrap = resBundleWrap;
    }

    @Override
    public String modify(String msg, Object ...args) {
        return resBundleWrap.getString(msg, args);
    }

}