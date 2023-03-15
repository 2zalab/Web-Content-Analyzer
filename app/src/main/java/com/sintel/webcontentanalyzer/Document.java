package com.sintel.webcontentanalyzer;

import java.util.HashMap;
import java.util.Map;

public class Document {

    /**
     * List of token counts
     */
    public Map<String, Integer> tokens;

    /**
     * The class of the document
     */
    public String category;

    /**
     * Document constructor
     */
    public Document() {
        tokens = new HashMap<>();
    }
}
