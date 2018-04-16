package ru.complitex.jedani.worker.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import ru.complitex.address.page.CityListPage;
import ru.complitex.address.page.CityTypeListPage;
import ru.complitex.address.page.CountryListPage;
import ru.complitex.address.page.RegionListPage;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.page.admin.ImportPage;
import ru.complitex.jedani.worker.page.resource.JedaniCssResourceReference;
import ru.complitex.jedani.worker.page.resource.JedaniJsResourceReference;
import ru.complitex.jedani.worker.page.resource.MenuCssResourceReference;
import ru.complitex.jedani.worker.page.worker.WorkerListPage;
import ru.complitex.jedani.worker.page.worker.WorkerPage;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.WorkerService;
import ru.complitex.name.page.FirstNameListPage;
import ru.complitex.name.page.LastNameListPage;
import ru.complitex.name.page.MiddleNameListPage;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Anatoly A. Ivanov
 * 22.11.2017 16:58
 */
@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class BasePage extends WebPage{
    @Inject
    private transient WorkerService workerService;

    protected BasePage() {
        add(new BookmarkablePageLink<>("home", HomePage.class));
        add(new BookmarkablePageLink<>("worker", WorkerPage.class));
        add(new BookmarkablePageLink<>("import", ImportPage.class));
        add(new BookmarkablePageLink<>("countries", CountryListPage.class));
        add(new BookmarkablePageLink<>("regions", RegionListPage.class));
        add(new BookmarkablePageLink<>("cityTypes", CityTypeListPage.class));
        add(new BookmarkablePageLink<>("cities", CityListPage.class));
        add(new BookmarkablePageLink<>("workers", WorkerListPage.class));
        add(new BookmarkablePageLink<>("first_name", FirstNameListPage.class));
        add(new BookmarkablePageLink<>("middle_name", MiddleNameListPage.class));
        add(new BookmarkablePageLink<>("last_name", LastNameListPage.class));

        String login = ((HttpServletRequest)getRequestCycle().getRequest().getContainerRequest()).getUserPrincipal().getName();

        Worker worker = workerService.getWorker(login);

        String fio = "";
        String jid = "";
        if (worker != null){
            fio = worker.getText(Worker.LAST_NAME) + " " +
                    worker.getText(Worker.SECOND_NAME) + " " +
                    worker.getText(Worker.FIRST_NAME) + " ";

            jid = worker.getText(Worker.J_ID);
        }

        add(new Label("fio", Model.of(fio)));

        add(new Label("jid", Model.of(jid)));

        add(new Label("login", Model.of(login)));

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

        response.render(CssHeaderItem.forReference(JedaniCssResourceReference.INSTANCE));
        response.render(CssHeaderItem.forReference(MenuCssResourceReference.INSTANCE));
        response.render(JavaScriptHeaderItem.forReference(JedaniJsResourceReference.INSTANCE));
    }


}