package com.pdfgen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jsoup.nodes.Document;

interface DataParser {

    Document parseTemplate(InputStream template) throws IOException;

    Map<String, Object> parseInputData(File file) throws IOException;

    org.w3c.dom.Document makeW3CDocument(Document dom);

}