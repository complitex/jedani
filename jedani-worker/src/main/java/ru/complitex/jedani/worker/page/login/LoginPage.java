package ru.complitex.jedani.worker.page.login;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.jedani.worker.page.resource.JedaniCssResourceReference;
import ru.complitex.jedani.worker.page.resource.JedaniLogoImgResourceReference;

/**
 * @author Anatoly A. Ivanov
 * 29.12.2017 19:25
 */
public class LoginPage extends WebPage{
    public LoginPage(PageParameters parameters) {
        setVersioned(false);

        add(new Image("jedani", JedaniLogoImgResourceReference.INSTANCE));

        add(new WebMarkupContainer("error"){
            @Override
            public boolean isVisible() {
                return !parameters.get("error").isNull();
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(JedaniCssResourceReference.INSTANCE));
    }
}
