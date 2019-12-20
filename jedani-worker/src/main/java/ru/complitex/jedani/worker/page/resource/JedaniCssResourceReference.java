package ru.complitex.jedani.worker.page.resource;

import de.agilecoders.wicket.core.markup.html.themes.bootstrap.BootstrapCssReference;
import de.agilecoders.wicket.themes.markup.html.google.GoogleCssReference;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 11.04.2018 16:46
 */
public class JedaniCssResourceReference extends CssResourceReference {
    public final static JedaniCssResourceReference INSTANCE = new JedaniCssResourceReference();

    private JedaniCssResourceReference() {
        super(JedaniCssResourceReference.class, "css/jedani.css");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        return Arrays.asList(CssReferenceHeaderItem.forReference(BootstrapCssReference.instance()),
                CssReferenceHeaderItem.forReference(GoogleCssReference.instance()));
    }
}
