package com.pdfgen;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.nodes.Document;

interface DataPopulator {

    void setLocale(Locale locale);

    void populateGenerationDate(Document doc);

    void populateData(Document doc, Map<String, Object> data, List<String> exclude);

}