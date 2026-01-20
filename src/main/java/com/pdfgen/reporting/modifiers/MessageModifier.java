package com.pdfgen.reporting.modifiers;

public interface MessageModifier {

    String modify(String msg, Object ...args);

}