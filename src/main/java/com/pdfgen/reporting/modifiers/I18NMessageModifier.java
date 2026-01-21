package com.pdfgen.reporting.modifiers;

import com.pdfgen.ResourceBundleWrapper;
import com.pdfgen.StandardResourceBundleWrapper;

public class I18NMessageModifier implements MessageModifier {

    private final ResourceBundleWrapper resBundleWrap = 
        new StandardResourceBundleWrapper("i18n.Messages");

    @Override
    public String modify(String msg, Object ...args) {
        return resBundleWrap.getString(msg, args);
    }

}