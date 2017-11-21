package ru.complitex.jedani.web;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * @author Anatoly A. Ivanov
 * 21.11.2017 15:04
 */
public class JedaniWebApplication extends WebApplication{
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }
}
