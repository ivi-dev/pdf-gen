package com.pdfgen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.pdfgen.converters.FontDeclaration;

interface DocumentBuilder {

    org.w3c.dom.Document makeW3CDoc(InputStream template, boolean verbose) throws IOException;

    PdfRendererBuilder init(org.w3c.dom.Document w3cDoc, FileOutputStream os);

    void setDataFile(File value);

    void setLocale(Locale value);

    void setFont(List<FontDeclaration> value);
    
    void resetFont();

}