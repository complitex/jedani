package ru.complitex.jedani.worker.page.login;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Anatoly A. Ivanov
 * 29.12.2017 19:25
 */
public class LoginPage extends WebPage{
    public LoginPage(PageParameters parameters) {
        add(new WebMarkupContainer("error"){
            @Override
            public boolean isVisible() {
                return !parameters.get("error").isNull();
            }
        });
    }
}
