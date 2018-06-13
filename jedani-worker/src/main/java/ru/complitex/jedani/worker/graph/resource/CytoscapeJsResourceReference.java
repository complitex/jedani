package ru.complitex.jedani.worker.graph.resource;

import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * @author Anatoly A. Ivanov
 * 13.06.2018 17:56
 */
public class CytoscapeJsResourceReference extends JavaScriptResourceReference {
    public static final CytoscapeJsResourceReference INSTANCE = new CytoscapeJsResourceReference();

    private CytoscapeJsResourceReference() {
        super(CytoscapeJsResourceReference.class, "js/cytoscape.min.js");
    }
}
