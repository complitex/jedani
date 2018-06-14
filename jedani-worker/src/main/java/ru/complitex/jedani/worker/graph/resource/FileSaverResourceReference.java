package ru.complitex.jedani.worker.graph.resource;

import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * @author Anatoly A. Ivanov
 * 14.06.2018 18:42
 */
public class FileSaverResourceReference extends JavaScriptResourceReference {
    public static final FileSaverResourceReference INSTANCE = new FileSaverResourceReference();

    public FileSaverResourceReference() {
        super(FileSaverResourceReference.class, "js/FileSaver.js");
    }
}
