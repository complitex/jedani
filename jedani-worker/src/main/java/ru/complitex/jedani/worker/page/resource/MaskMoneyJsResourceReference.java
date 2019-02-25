package ru.complitex.jedani.worker.page.resource;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.Collections;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 26.02.2019 11:56
 */
public class MaskMoneyJsResourceReference extends JavaScriptResourceReference {
    public static final MaskMoneyJsResourceReference INSTANCE = new MaskMoneyJsResourceReference();

    public MaskMoneyJsResourceReference() {
        super(MaskMoneyJsResourceReference.class, "js/jquery.maskMoney.min.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        return Collections.singletonList(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
    }
}
