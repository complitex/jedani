package ru.complitex.jedani.worker.page.resource;

import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * @author Anatoly A. Ivanov
 * 19.03.2019 15:54
 */
public class ApexChartsJsResourceReference extends JavaScriptResourceReference {
    public static final ApexChartsJsResourceReference INSTANCE = new ApexChartsJsResourceReference();

    public ApexChartsJsResourceReference(){
        super(ApexChartsJsResourceReference.class, "js/apexcharts.min.js");
    }
}
