package ru.complitex.jedani.worker.page.resource;

import de.agilecoders.wicket.core.markup.html.references.BootstrapJavaScriptReference;
import de.agilecoders.wicket.core.markup.html.references.JQueryMigrateJavaScriptReference;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 11.04.2018 16:26
 */
public class JedaniJsResourceReference extends JavaScriptResourceReference {
    public static final JedaniJsResourceReference INSTANCE = new JedaniJsResourceReference();

    private JedaniJsResourceReference() {
        super(JedaniJsResourceReference.class, "js/jedani.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        return Arrays.asList(
                JavaScriptHeaderItem.forReference(JQueryResourceReference.getV3()),
                JavaScriptHeaderItem.forReference(MenuJsResourceReference.INSTANCE),
                JavaScriptHeaderItem.forReference(JQueryMigrateJavaScriptReference.instance()),
                JavaScriptHeaderItem.forReference(BootstrapJavaScriptReference.instance()));
    }
}
