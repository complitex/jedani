package ru.complitex.jedani.worker.page.resource;

import org.apache.wicket.request.resource.CssResourceReference;

/**
 * @author Anatoly A. Ivanov
 * 11.04.2018 16:42
 */
public class MenuCssResourceReference extends CssResourceReference {
    public static final MenuCssResourceReference INSTANCE = new MenuCssResourceReference();

    private MenuCssResourceReference() {
        super(MenuCssResourceReference.class, "css/metisMenu.min.css");
    }
}
