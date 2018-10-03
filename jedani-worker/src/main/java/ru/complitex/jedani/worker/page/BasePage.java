package ru.complitex.jedani.worker.page;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import ru.complitex.address.page.CityListPage;
import ru.complitex.address.page.CityTypeListPage;
import ru.complitex.address.page.CountryListPage;
import ru.complitex.address.page.RegionListPage;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.page.admin.ImportPage;
import ru.complitex.jedani.worker.page.catalog.MkStatusListPage;
import ru.complitex.jedani.worker.page.catalog.PositionListPage;
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
import ru.complitex.name.service.NameService;
import ru.complitex.user.entity.User;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Anatoly A. Ivanov
 * 22.11.2017 16:58
 */
@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class BasePage extends WebPage{
    @Inject
    private WorkerService workerService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private NameService nameService;

    private User currentUser;

    private Worker currentWorker;

    protected BasePage() {
        String login = ((HttpServletRequest)getRequestCycle().getRequest().getContainerRequest()).getUserPrincipal().getName();

        currentUser = userMapper.getUser(login);
        currentWorker = workerService.getWorker(login);

        add(new BookmarkablePageLink<>("home", HomePage.class));

        String fio = "";
        String jid = "";
        if (currentWorker != null){
            fio = nameService.getLastName(currentWorker.getNumber(Worker.LAST_NAME)) + " " +
                    nameService.getFirstName(currentWorker.getNumber(Worker.FIRST_NAME)) + " " +
                    nameService.getMiddleName(currentWorker.getNumber(Worker.MIDDLE_NAME));

            jid = currentWorker.getText(Worker.J_ID);
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

        add(new BookmarkablePageLink<>("worker", WorkerPage.class));

        WebMarkupContainer settings = new WebMarkupContainer("settings");
        settings.setVisible(isAdmin());
        add(settings);

        settings.add(new BookmarkablePageLink<>("import", ImportPage.class).setVisible(isAdmin()));

        WebMarkupContainer address = new WebMarkupContainer("address");
        address.setVisible(isAdmin());
        add(address);

        address.add(new BookmarkablePageLink<>("countries", CountryListPage.class).setVisible(isAdmin()));
        address.add(new BookmarkablePageLink<>("regions", RegionListPage.class).setVisible(isAdmin()));
        address.add(new BookmarkablePageLink<>("cityTypes", CityTypeListPage.class).setVisible(isAdmin()));
        address.add(new BookmarkablePageLink<>("cities", CityListPage.class).setVisible(isAdmin()));

        WebMarkupContainer catalog = new WebMarkupContainer("catalog");
        catalog.setVisible(isAdmin() || isStructureAdmin());
        add(catalog);

        catalog.add(new BookmarkablePageLink<>("first_name", FirstNameListPage.class).setVisible(isAdmin()));
        catalog.add(new BookmarkablePageLink<>("middle_name", MiddleNameListPage.class).setVisible(isAdmin()));
        catalog.add(new BookmarkablePageLink<>("last_name", LastNameListPage.class).setVisible(isAdmin()));
        catalog.add(new BookmarkablePageLink<>("position", PositionListPage.class).setVisible(isAdmin()));
        catalog.add(new BookmarkablePageLink<>("mk_status", MkStatusListPage.class).setVisible(isAdmin()));
        catalog.add(new BookmarkablePageLink<>("workers", WorkerListPage.class).setVisible(isAdmin() || isStructureAdmin()));

        add(new WebMarkupContainer("nomenclature").setVisible(isAdmin()));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(JedaniCssResourceReference.INSTANCE));
        response.render(CssHeaderItem.forReference(MenuCssResourceReference.INSTANCE));
        response.render(JavaScriptHeaderItem.forReference(JedaniJsResourceReference.INSTANCE));
    }

    private HttpServletRequest getHttpServletRequest(){
        return ((HttpServletRequest)RequestCycle.get().getRequest().getContainerRequest());
    }

    protected String getLogin(){
        return getHttpServletRequest().getUserPrincipal().getName();
    }

    protected boolean isAdmin(){
        return getHttpServletRequest().isUserInRole(JedaniRoles.ADMINISTRATORS);
    }

    protected boolean isStructureAdmin(){
        return getHttpServletRequest().isUserInRole(JedaniRoles.STRUCTURE_ADMINISTRATORS);
    }

    protected boolean isUser(){
        return getHttpServletRequest().isUserInRole(JedaniRoles.USERS);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    protected Worker getCurrentWorker() {
        if (currentWorker == null && isAdmin()){
            currentWorker = new Worker();
            currentWorker.init();

            currentWorker.setParentId(getCurrentUser().getId());
        }

        return currentWorker;
    }
}
