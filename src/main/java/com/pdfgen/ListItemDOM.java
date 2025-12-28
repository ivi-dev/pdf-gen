package com.pdfgen;

import org.jsoup.nodes.Element;

record ListItemDOM(Element doc, Element template, Element container) { 

    private static final String COLLECTION_ITEM_TEMPLATE_SELECTOR = ".item-template";

    ListItemDOM(Element doc, String selector) {
        this(
            doc, 
            doc.getElementById(selector)
               .selectFirst(COLLECTION_ITEM_TEMPLATE_SELECTOR),
            doc.getElementById(selector)
        );
    }

    void cloneTemplate() {
        this.container().appendChild(this.template().clone());
    }

    void removeFirstItem() {
        this.container()
            .select(COLLECTION_ITEM_TEMPLATE_SELECTOR)
            .first()
            .remove();
    }


}