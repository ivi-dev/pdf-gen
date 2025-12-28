package com.pdfgen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@FunctionalInterface
interface TriConsumer<T1, T2, T3> {

    void consume(T1 arg, T2 arg2, T3 arg3);

}

public class DefaultDataPopulatorTest {

    // MOCK DATA'S STRUCTURE:
    // {
    //      "nestedMap": {
    //          "string": "Lorem ipsum...",
    //          "double": 1.2,
    //      },
    //      "nestedList": [
    //         {
    //              "img": "<base64>",
    //              "text": "Hello, world!"
    //         }
    //      ]
    // }

    private Map<String, Object> mockData;

    private DefaultDataPopulator dataPopulator;

    private Document mockDoc;

    private Element imgElement;
    
    private Element mockItemTemplate;
    
    private Element divElement;

    private Element mockFirstElement;
    
    private Elements mockElementCollection;

    private Element mockItemsContainer;
    
    private Element mockNestedItemElement;

    private void verifyPopulateGenerationDate(
        DefaultDataPopulator dataPopulator, 
        TriConsumer<Element, DateTimeFormatter, String> proc
    ) {
        var mockElement = mock(Element.class);
        when(mockDoc.selectFirst("#generation-date")).thenReturn(mockElement);
        try (var mocked = mockStatic(LocalDate.class)) {
            var formattedDate = "formatted-date";
            var mockNow = mock(LocalDate.class);
            when(mockNow.format(any())).thenReturn(formattedDate);
            mocked.when(LocalDate::now).thenReturn(mockNow);
            dataPopulator.populateGenerationDate(mockDoc);
            var formatCaptor = ArgumentCaptor.forClass(DateTimeFormatter.class);
            verify(mockNow).format(formatCaptor.capture());
            proc.consume(mockElement, formatCaptor.getValue(), formattedDate);
        }
    }

    @BeforeEach 
    void setUp() { 
        mockData = initData();
        dataPopulator = new DefaultDataPopulator(new Locale(("bg")));
        mockDoc = mock(Document.class);
        imgElement = mock(Element.class);
        when(imgElement.tagName()).thenReturn("img");
        mockItemTemplate = mock(Element.class);
        when(mockItemTemplate.selectFirst(".img")).thenReturn(imgElement);
        divElement = mock(Element.class);
        when(divElement.tagName()).thenReturn("div");
        when(mockItemTemplate.selectFirst(".text")).thenReturn(divElement);
        when(mockItemTemplate.clone()).thenReturn(mockItemTemplate);
        mockFirstElement = mock(Element.class);
        mockElementCollection = mock(Elements.class);
        when(mockElementCollection.first()).thenReturn(mockFirstElement);
        mockItemsContainer = mock(Element.class);
        when(mockItemsContainer.select(".item-template")).thenReturn(mockElementCollection);
        when(mockItemsContainer.selectFirst(".item-template")).thenReturn(mockItemTemplate);
        mockNestedItemElement = mock(Element.class);
        when(mockDoc.getElementById("nested-map-string")).thenReturn(mockNestedItemElement);
        when(mockDoc.getElementById("nested-map-double")).thenReturn(mockNestedItemElement);
        when(mockDoc.getElementById("nested-list")).thenReturn(mockItemsContainer);
    }

    private Map<String, Object> initData() {
        var data = new HashMap<String, Object>();
        var mockNestedMap = new HashMap<String, Object>();
        mockNestedMap.put("string", "Lorem ipsum...");
        mockNestedMap.put("double", 1.2);
        data.put("nestedMap", mockNestedMap);
        var mockNestedListedItem = new HashMap<String, Object>();
        mockNestedListedItem.put("img", "<base64>");
        mockNestedListedItem.put("text", "Hello, world!");
        data.put("nestedList", List.of(mockNestedListedItem));
        return data;
    }

    @Test
    void constructorInitializesObject() {
        assertNotNull(dataPopulator);
    }

    @Test
    void setLocaleChangesTheLocale() {
        var newLocale = new Locale("en");
        dataPopulator.setLocale(newLocale);
        verifyPopulateGenerationDate(
            dataPopulator, 
            (mockElement, dateFormatter, formattedDate) -> 
                assertEquals(newLocale, dateFormatter.getLocale())
        );
    }

    @Test
    void populateGenerationDateInsertsCurrentDateIntoDocument() {
        verifyPopulateGenerationDate(
            dataPopulator, 
            (mockElement, dateFormatter, formattedDate) -> 
                verify(mockElement).text(formattedDate)
        );
    }

    @Test
    void populateDataWithoutExclusionsPopulatesDocumentWithAllData() {
        dataPopulator.populateData(mockDoc, mockData, null);
        // Verify nested list items population
        verify(imgElement, times(1)).attr(
            "src", 
            "data:image/svg+xml;base64,<base64>"
        );
        verify(divElement, times(1)).html("Hello, world!");
        verify(mockFirstElement).remove();
        // Verify nested map items population
        verify(mockNestedItemElement, times(1)).html("Lorem ipsum...");
        verify(mockNestedItemElement, times(1)).html("1,2");
    }

    @Test
    void populateDataWithExclusionsPopulatesDocumentWithOnlyNonExcludedData() {
        dataPopulator.populateData(mockDoc, mockData, List.of("nestedList"));
        // Verify nested list items NOT populated
        verify(imgElement, never()).attr(
            "src", 
            "data:image/svg+xml;base64,<base64>"
        );
        verify(divElement, never()).html("Hello, world!");
        verify(mockFirstElement, never()).remove();
        // Verify nested map items population
        verify(mockNestedItemElement).html("Lorem ipsum...");
        verify(mockNestedItemElement).html("1,2");
    }

}
