package ru.complitex.jedani.user.web;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.resource.PackageResourceReference;
import ru.complitex.jedani.user.web.catalog.ImportPage;
import ru.complitex.jedani.user.web.user.UserListPage;

/**
 * @author Anatoly A. Ivanov
 * 22.11.2017 16:58
 */
public class BasePage extends WebPage{
    public BasePage() {
        add(new BookmarkablePageLink<>("import", ImportPage.class)); //todo class="active"
        add(new BookmarkablePageLink<>("users", UserListPage.class));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(new PackageResourceReference(HomePage.class, "/css/jedani.css")));
    }
}
