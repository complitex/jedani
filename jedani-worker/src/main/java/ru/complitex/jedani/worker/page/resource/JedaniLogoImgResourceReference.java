package ru.complitex.jedani.worker.page.resource;

import org.apache.wicket.request.resource.SharedResourceReference;

/**
 * @author Anatoly A. Ivanov
 * 14.04.2019 20:28
 */
public class JedaniLogoImgResourceReference extends SharedResourceReference {
    public static final JedaniLogoImgResourceReference INSTANCE = new JedaniLogoImgResourceReference();

    public JedaniLogoImgResourceReference() {
        super(JedaniLogoImgResourceReference.class, "img/jedani.svg");
    }
}
