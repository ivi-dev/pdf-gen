package com.pdfgen;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.nodes.Document;

class DefaultDataPopulator implements DataPopulator {

    private DateTimeFormatter dateTimeFormatter;

    private NumberFormat numFormatter;

    DefaultDataPopulator(Locale locale) {
        dateTimeFormatter = DateTimeFormatter
                            .ofLocalizedDate(FormatStyle.MEDIUM)
                            .withLocale(locale);
        numFormatter = NumberFormat.getInstance(locale);
    }

    @Override
    public void setLocale(Locale locale) {
        dateTimeFormatter = DateTimeFormatter
                            .ofLocalizedDate(FormatStyle.MEDIUM)
                            .withLocale(locale);
        numFormatter = NumberFormat.getInstance(locale);
    }

    @Override
    public void populateGenerationDate(Document doc) {
        var formattedDate = LocalDate.now().format(dateTimeFormatter);
        doc.selectFirst("#generation-date").text(formattedDate);
    }

    private static String camelToKebap(String value) {
        return value.replaceAll("([a-z])([A-Z]+)", "$1-$2")
                    .toLowerCase();
    }

    private static String snakeToKebap(String value) {
        return value.replaceAll("_", "-");
    }

    private void processDataItem(
        Document doc, 
        String key, 
        Object value, 
        String selector, 
        List<String> exclude,
        ListItemDOM listItemDOM
    ) {
        var cleanKey = snakeToKebap(camelToKebap(key));
        var selector_ = selector == null ? 
                        cleanKey : 
                        selector + "-" + cleanKey;
        if (value instanceof Map) {
            processMap(
                doc, 
                value, 
                exclude, 
                listItemDOM, 
                selector_
            );
        } else if (value instanceof List) {
            processList(
                doc, 
                value, 
                exclude, 
                selector_
            );
        } else {
            processScalar(
                doc, 
                value, 
                listItemDOM, 
                cleanKey, 
                selector_
            );
        }
    }

    private void processMap(
        Document doc, 
        Object map, 
        List<String> exclude, 
        ListItemDOM listItemDOM, 
        String selector
    ) {
        @SuppressWarnings("unchecked")
        var nestedMap = (Map<String, Object>) map;
        populateData(
            doc, 
            nestedMap, 
            selector, 
            exclude, 
            listItemDOM
        );
    }

    private void processList(
        Document doc, 
        Object value, 
        List<String> exclude, 
        String selector_
    ) {
        @SuppressWarnings("unchecked")
        var nestedList = (List<Object>) value;
        var listItemDOM_ = new ListItemDOM(doc, selector_);
        for (var it : nestedList) {
            processMap(
                doc, 
                it, 
                exclude, 
                listItemDOM_, 
                selector_
            );
            listItemDOM_.cloneTemplate();
        }
        listItemDOM_.removeFirstItem();
    }

    private void processScalar(
        Document doc, 
        Object value, 
        ListItemDOM listItemDOM, 
        String cleanKey, 
        String selector_
    ) {
        var value_ = value instanceof Double ? 
                     numFormatter.format((double) value) : 
                     value.toString();
        if (listItemDOM != null) {
            populateListItem(listItemDOM, cleanKey, value_);
        } else {
            doc.getElementById(selector_).html(value_.toString());
        }
    }

    private void populateListItem(
        ListItemDOM listItemDOM, 
        String cleanKey, 
        String value
    ) {
        var el = listItemDOM.template().selectFirst("." + cleanKey);
        if(el.tagName() == "img") {
            el.attr(
                "src", 
                "data:image/svg+xml;base64," + value
            );
        } else {
            el.html(value.toString());
        }
    }

    @Override
    public void populateData(
        Document doc, 
        Map<String, Object> data, 
        List<String> exclude
    ) {
        populateData(
            doc, 
            data, 
            null, 
            exclude, 
            null
        );
    }

    private void populateData(
        Document doc, 
        Map<String, Object> data, 
        String selector, 
        List<String> exclude,
        ListItemDOM listItemDOM
    ) {
        data.forEach((key, value) -> {
            if (exclude != null) {
                if (!exclude.contains(key)) {
                    processDataItem(
                        doc, 
                        key, 
                        value, 
                        selector, 
                        exclude, 
                        listItemDOM
                    );
                }
            } else {
                processDataItem(
                    doc, 
                    key, 
                    value, 
                    selector, 
                    exclude, 
                    listItemDOM
                );
            }
        });
    }

}