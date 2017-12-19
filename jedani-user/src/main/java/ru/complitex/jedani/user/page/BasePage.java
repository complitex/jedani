package ru.complitex.jedani.user.page;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.resource.PackageResourceReference;
import ru.complitex.address.page.CityListPage;
import ru.complitex.address.page.RegionListPage;
import ru.complitex.jedani.user.page.catalog.ImportPage;

/**
 * @author Anatoly A. Ivanov
 * 22.11.2017 16:58
 */
public class BasePage extends WebPage{
    public BasePage() {
        add(new BookmarkablePageLink<>("import", ImportPage.class)); //todo class="active"
        add(new BookmarkablePageLink<>("regions", RegionListPage.class));
        add(new BookmarkablePageLink<>("cities", CityListPage.class));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(new PackageResourceReference(HomePage.class, "/css/jedani.css")));
    }
}
