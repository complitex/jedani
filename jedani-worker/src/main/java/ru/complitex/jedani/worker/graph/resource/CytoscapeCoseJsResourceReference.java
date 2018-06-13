package ru.complitex.jedani.worker.graph.resource;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.Collections;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 13.06.2018 17:59
 */
public class CytoscapeCoseJsResourceReference extends JavaScriptResourceReference {
    public static final CytoscapeCoseJsResourceReference INSTANCE = new CytoscapeCoseJsResourceReference();

    private CytoscapeCoseJsResourceReference() {
        super(CytoscapeCoseJsResourceReference.class, "js/cytoscape-cose-bilkent.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        return Collections.singletonList(JavaScriptHeaderItem.forReference(CytoscapeJsResourceReference.INSTANCE));
    }
}
