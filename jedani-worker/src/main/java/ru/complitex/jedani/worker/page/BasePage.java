package ru.complitex.jedani.worker.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import ru.complitex.address.page.CityListPage;
import ru.complitex.address.page.CityTypeListPage;
import ru.complitex.address.page.CountryListPage;
import ru.complitex.address.page.RegionListPage;
import ru.complitex.common.wicket.application.ServletWebSession;
import ru.complitex.jedani.worker.page.admin.ImportPage;
import ru.complitex.jedani.worker.page.worker.WorkerListPage;
import ru.complitex.jedani.worker.page.worker.WorkerPage;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.name.page.FirstNameListPage;
import ru.complitex.name.page.LastNameListPage;
import ru.complitex.name.page.MiddleNameListPage;

/**
 * @author Anatoly A. Ivanov
 * 22.11.2017 16:58
 */
@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class BasePage extends WebPage{
    protected BasePage() {
        add(new BookmarkablePageLink<>("home", HomePage.class));
        add(new BookmarkablePageLink<>("worker", WorkerPage.class, new PageParameters().add("id", 17)));
        add(new BookmarkablePageLink<>("import", ImportPage.class)); //todo class="active"
        add(new BookmarkablePageLink<>("countries", CountryListPage.class));
        add(new BookmarkablePageLink<>("regions", RegionListPage.class));
        add(new BookmarkablePageLink<>("cityTypes", CityTypeListPage.class));
        add(new BookmarkablePageLink<>("cities", CityListPage.class));
        add(new BookmarkablePageLink<>("workers", WorkerListPage.class));
        add(new BookmarkablePageLink<>("first_name", FirstNameListPage.class));
        add(new BookmarkablePageLink<>("middle_name", MiddleNameListPage.class));
        add(new BookmarkablePageLink<>("last_name", LastNameListPage.class));

        String login = ((ServletWebSession)getSession()).getRequest().getContainerRequest().getUserPrincipal().getName();

        add(new Label("login", login));

        add(new Link<Void>("logout") {
            @Override
            public void onClick() {
                getSession().invalidate();
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(new PackageResourceReference(HomePage.class, "/css/jedani.css")));
    }


}
