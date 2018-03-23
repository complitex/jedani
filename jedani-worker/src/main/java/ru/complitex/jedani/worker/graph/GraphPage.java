package ru.complitex.jedani.worker.graph;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * @author Anatoly A. Ivanov
 * 22.03.2018 17:00
 */
public class GraphPage extends WebPage {
    public GraphPage() {
    }

    @Override
    public void renderHead(IHeaderResponse response){
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(GraphPage.class, "js/cytoscape.js")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(GraphPage.class, "js/graph.js")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(GraphPage.class, "css/graph.css")));
    }


}
