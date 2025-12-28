package com.pdfgen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.databind.ObjectMapper;

class DefaultDataParser implements DataParser {

    @Override
    public Document parseTemplate(InputStream template) throws IOException {
        return Jsoup.parse(template, "UTF-8", "");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> parseInputData(File file) throws IOException {
        return new ObjectMapper().readValue(file, Map.class);
    }

    @Override
    public org.w3c.dom.Document makeW3CDocument(Document dom) {
        return new W3CDom().fromJsoup(dom);
    }

}