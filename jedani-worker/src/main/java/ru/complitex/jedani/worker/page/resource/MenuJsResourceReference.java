package ru.complitex.jedani.worker.page.resource;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import java.util.Collections;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 11.04.2018 16:36
 */
public class MenuJsResourceReference extends JavaScriptResourceReference {
    public static final ResourceReference INSTANCE = new MenuJsResourceReference();

    private MenuJsResourceReference() {
        super(MenuJsResourceReference.class, "js/metisMenu.min.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        return Collections.singletonList(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
    }
}

