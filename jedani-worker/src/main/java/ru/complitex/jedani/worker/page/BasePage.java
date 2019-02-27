package ru.complitex.jedani.worker.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.list.BootstrapListView;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import ru.complitex.address.entity.City;
import ru.complitex.address.page.CityListPage;
import ru.complitex.address.page.CityTypeListPage;
import ru.complitex.address.page.CountryListPage;
import ru.complitex.address.page.RegionListPage;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.wicket.component.MenuLink;
import ru.complitex.domain.service.DomainService;
import ru.complitex.domain.util.Attributes;
import ru.complitex.jedani.worker.entity.Storage;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.jedani.worker.mapper.StorageMapper;
import ru.complitex.jedani.worker.page.admin.ImportPage;
import ru.complitex.jedani.worker.page.admin.SettingPage;
import ru.complitex.jedani.worker.page.catalog.CurrencyListPage;
import ru.complitex.jedani.worker.page.catalog.ExchangeRateList;
import ru.complitex.jedani.worker.page.catalog.MkStatusListPage;
import ru.complitex.jedani.worker.page.catalog.PositionListPage;
import ru.complitex.jedani.worker.page.promotion.PromotionListPage;
import ru.complitex.jedani.worker.page.resource.JedaniCssResourceReference;
import ru.complitex.jedani.worker.page.resource.JedaniJsResourceReference;
import ru.complitex.jedani.worker.page.resource.MenuCssResourceReference;
import ru.complitex.jedani.worker.page.resource.MenuJsResourceReference;
import ru.complitex.jedani.worker.page.sale.SaleListPage;
import ru.complitex.jedani.worker.page.storage.NomenclatureListPage;
import ru.complitex.jedani.worker.page.storage.StorageListPage;
import ru.complitex.jedani.worker.page.storage.StoragePage;
import ru.complitex.jedani.worker.page.worker.WorkerListPage;
import ru.complitex.jedani.worker.page.worker.WorkerPage;
import ru.complitex.jedani.worker.security.JedaniRoles;
import ru.complitex.jedani.worker.service.StorageService;
import ru.complitex.jedani.worker.service.WorkerService;
import ru.complitex.name.page.FirstNameListPage;
import ru.complitex.name.page.LastNameListPage;
import ru.complitex.name.page.MiddleNameListPage;
import ru.complitex.name.service.NameService;
import ru.complitex.user.entity.User;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Anatoly A. Ivanov
 * 22.11.2017 16:58
 */
@AuthorizeInstantiation(JedaniRoles.AUTHORIZED)
public class BasePage extends WebPage{
    private static final MetaDataKey<HashMap<String, String>> MENU_TOGGLE = new MetaDataKey<HashMap<String, String>>() {};

    @Inject
    private WorkerService workerService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private NameService nameService;

    @Inject
    private StorageMapper storageMapper;

    @Inject
    private StorageService storageService;

    @Inject
    private DomainService domainService;

    private User currentUser;

    private Worker currentWorker;

    protected BasePage() {
        String login = ((HttpServletRequest)getRequestCycle().getRequest().getContainerRequest()).getUserPrincipal().getName();

        currentUser = userMapper.getUser(login);
        currentWorker = workerService.getWorker(login);

        add(new MenuLink("home", HomePage.class));

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

        WebMarkupContainer sidebar = new WebMarkupContainer("sidebar");
        sidebar.add(newAjaxEventMenuBehavior());
        add(sidebar);

        sidebar.add(new MenuLink("worker", WorkerPage.class));

        WebMarkupContainer userStorages = new WebMarkupContainer("userStorages");
        userStorages.setVisible(isUser() && !isAdmin() && !isParticipant());
        userStorages.add(newBehavior());
        sidebar.add(userStorages);

        userStorages.add(new MenuLink("storage", StoragePage.class,
                new PageParameters().add("id", getCurrentStorage().getObjectId())));

        userStorages.add(new BootstrapListView<Storage>("storages", new LoadableDetachableModel<List<Storage>>() {
            @Override
            protected List<Storage> load() {
                return storageMapper.getStorages(new FilterWrapper<>(new Storage())
                        .add(Storage.FILTER_CURRENT_WORKER, getCurrentWorker().getObjectId())
                        .add(Storage.FILTER_CITIES, getCurrentWorker().getNumberValuesString(Worker.CITIES)))
                        .stream().filter(s -> s.getNumber(Storage.CITY) != null).collect(Collectors.toList());
            }
        }) {
            @Override
            protected void populateItem(ListItem<Storage> item) {
                Storage storage = item.getModelObject();

                String label = Attributes.capitalize(domainService.getDomain(City.class, storage.getNumber(Storage.CITY))
                        .getTextValue(City.NAME)); //todo get value text sql

                item.add(new MenuLink("link", StoragePage.class,
                        new PageParameters().add("id", storage.getObjectId()))
                        .add(new Label("label", label)));
            }
        }.setVisible(isUser() || isStructureAdmin()));

        WebMarkupContainer settings = new WebMarkupContainer("settings");
        settings.setVisible(isAdmin());
        settings.add(newBehavior());
        sidebar.add(settings);

        settings.add(new MenuLink("setting", SettingPage.class).setVisible(isAdmin()));
        settings.add(new MenuLink("import", ImportPage.class).setVisible(isAdmin()));

        WebMarkupContainer address = new WebMarkupContainer("address");
        address.setVisible(isAdmin());
        address.add(newBehavior());
        sidebar.add(address);

        address.add(new MenuLink("countries", CountryListPage.class).setVisible(isAdmin()));
        address.add(new MenuLink("regions", RegionListPage.class).setVisible(isAdmin()));
        address.add(new MenuLink("cityTypes", CityTypeListPage.class).setVisible(isAdmin()));
        address.add(new MenuLink("cities", CityListPage.class).setVisible(isAdmin()));

        WebMarkupContainer catalog = new WebMarkupContainer("catalog");
        catalog.setVisible(isAdmin() || isStructureAdmin());
        catalog.add(newBehavior());
        sidebar.add(catalog);

        catalog.add(new MenuLink("first_name", FirstNameListPage.class).setVisible(isAdmin()));
        catalog.add(new MenuLink("middle_name", MiddleNameListPage.class).setVisible(isAdmin()));
        catalog.add(new MenuLink("last_name", LastNameListPage.class).setVisible(isAdmin()));
        catalog.add(new MenuLink("position", PositionListPage.class).setVisible(isAdmin()));
        catalog.add(new MenuLink("mk_status", MkStatusListPage.class).setVisible(isAdmin()));
        catalog.add(new MenuLink("currency", CurrencyListPage.class).setVisible(isAdmin()));
        catalog.add(new MenuLink("exchangeRate", ExchangeRateList.class).setVisible(isAdmin()));

        WebMarkupContainer workers = new WebMarkupContainer("workers");
        workers.setVisible(isAdmin() || isStructureAdmin());
        workers.add(newBehavior());
        sidebar.add(workers);

        workers.add(new MenuLink("workers", WorkerListPage.class).addMenuPageClass(WorkerPage.class));

        WebMarkupContainer storages = new WebMarkupContainer("storages");
        storages.setVisible(isAdmin());
        storages.add(newBehavior());
        sidebar.add(storages);

        storages.add(new MenuLink("nomenclature", NomenclatureListPage.class));
        storages.add(new MenuLink("storage", StorageListPage.class).addMenuPageClass(StoragePage.class));

        WebMarkupContainer promotions = new WebMarkupContainer("promotions");
        promotions.setVisible(isAdmin() || isPromotionAdmin());
        promotions.add(newBehavior());
        sidebar.add(promotions);

        promotions.add(new MenuLink("promotion", PromotionListPage.class));

        WebMarkupContainer sales = new WebMarkupContainer("sales");
        sales.add(newBehavior());
        sidebar.add(sales);

        sales.add(new MenuLink("sale", SaleListPage.class));

        sidebar.visitChildren(MenuLink.class, (IVisitor<MenuLink, IVisit>) (m, v) -> {
            if (getClass().equals(m.getPageClass()) || m.hasMenuPageClass(getClass())){
                m.add(new AttributeAppender("class", "selected"));

                v.stop();
            }
        });
    }

    private Behavior newBehavior() {
        return new Behavior() {
            @Override
            public void onComponentTag(Component component, ComponentTag tag) {
                String _class = getMenuToggleMap().get(component.getId());

                if (_class != null && _class.isEmpty()){
                    de.agilecoders.wicket.core.util.Attributes.removeClass(tag, "mm-active");
                }
            }
        };
    }

    private AjaxEventBehavior newAjaxEventMenuBehavior() {
        return new AjaxEventBehavior("shown.metisMenu hidden.metisMenu") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                IRequestParameters requestParameters = getComponent().getRequest().getRequestParameters();

                String id = requestParameters.getParameterValue("id").toOptionalString();
                String _class = requestParameters.getParameterValue("class").toOptionalString();

                getMenuToggleMap().put(id, _class);
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);

                attributes.getDynamicExtraParameters().add("return {'id' : $(attrs.event.target).parent('li').prop('id')}");
                attributes.getDynamicExtraParameters().add("return {'class' : $(attrs.event.target).parent('li').prop('class')}");
            }
        };
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(JedaniCssResourceReference.INSTANCE));
        response.render(JavaScriptHeaderItem.forReference(JedaniJsResourceReference.INSTANCE));

        response.render(CssHeaderItem.forReference(MenuCssResourceReference.INSTANCE));
        response.render(JavaScriptHeaderItem.forReference(MenuJsResourceReference.INSTANCE));

        response.render(OnDomReadyHeaderItem.forScript("$('#menu').metisMenu({toggle: true})"));
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

    protected boolean isPromotionAdmin(){
        return getHttpServletRequest().isUserInRole(JedaniRoles.PROMOTION_ADMINISTRATORS);
    }

    protected boolean isUser(){
        return getHttpServletRequest().isUserInRole(JedaniRoles.USERS);
    }

    protected boolean isParticipant(){
        return Objects.equals(getCurrentWorker().getNumber(Worker.TYPE), 1L);
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

    protected Storage getCurrentStorage(){
        List<Storage> storages = storageMapper.getStorages(FilterWrapper.of((Storage) new Storage()
                .setParentId(getCurrentWorker().getObjectId())));

        if (storages.isEmpty() && !isParticipant()){
            return storageService.createVirtualStorage(getCurrentWorker().getObjectId());
        }

        return storages.get(0);
    }

    private Map<String, String> getMenuToggleMap(){
        HashMap<String, String> map =  getSession().getMetaData(MENU_TOGGLE);

        if (map == null){
            map = new HashMap<>();

            getSession().setMetaData(MENU_TOGGLE, map);
        }

        return map;
    }


}
