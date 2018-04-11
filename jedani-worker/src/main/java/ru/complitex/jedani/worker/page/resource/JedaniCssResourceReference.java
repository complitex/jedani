package ru.complitex.jedani.worker.page.resource;

import org.apache.wicket.request.resource.CssResourceReference;

/**
 * @author Anatoly A. Ivanov
 * 11.04.2018 16:46
 */
public class JedaniCssResourceReference extends CssResourceReference {
    public final static JedaniCssResourceReference INSTANCE = new JedaniCssResourceReference();

    private JedaniCssResourceReference() {
        super(JedaniCssResourceReference.class, "css/jedani.css");
    }
}
