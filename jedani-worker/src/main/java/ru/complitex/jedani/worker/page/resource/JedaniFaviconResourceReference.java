package ru.complitex.jedani.worker.page.resource;

import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * @author Anatoly A. Ivanov
 * 30.04.2019 17:19
 */
public class JedaniFaviconResourceReference extends PackageResourceReference {
    public static final JedaniFaviconResourceReference INSTANCE = new JedaniFaviconResourceReference();

    public JedaniFaviconResourceReference() {
        super(JedaniFaviconResourceReference.class, "img/favicon.ico");
    }
}
