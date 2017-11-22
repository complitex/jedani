package ru.complitex.jedani.web;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.resource.PackageResourceReference;
import ru.complitex.jedani.mapper.UserMapper;

import javax.inject.Inject;

/**
 * @author Anatoly A. Ivanov
 * 21.11.2017 15:04
 */
public class HomePage extends WebPage{
    @Inject
    private UserMapper userMapper;

    public HomePage() {
        add(new Label("test", userMapper.ping()));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(new PackageResourceReference(HomePage.class, "/css/jedani.css")));
    }
}
