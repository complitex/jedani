package ru.complitex.jedani.worker.page;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.references.BootstrapJavaScriptReference;
import de.agilecoders.wicket.core.markup.html.references.JQueryMigrateJavaScriptReference;
import de.agilecoders.wicket.core.markup.html.themes.bootstrap.BootstrapCssReference;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.core.settings.SingleThemeProvider;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.SelectCSSReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.SelectJSReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.references.BootstrapDatepickerJsReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.references.BootstrapDatepickerLangJsReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.references.BootstrapDatepickerReference;
import de.agilecoders.wicket.themes.markup.html.google.GoogleCssReference;
import de.agilecoders.wicket.themes.markup.html.google.GoogleTheme;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.WicketAjaxJQueryResourceReference;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.cdi.ConversationPropagation;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteBehavior;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;
import ru.complitex.address.page.CityListPage;
import ru.complitex.address.page.CountryListPage;
import ru.complitex.address.page.RegionListPage;
import ru.complitex.common.wicket.application.ServletAuthorizationStrategy;
import ru.complitex.common.wicket.application.ServletUnauthorizedListener;
import ru.complitex.common.wicket.application.ServletWebSession;
import ru.complitex.jedani.worker.graph.GraphPage;
import ru.complitex.jedani.worker.page.admin.ImportPage;
import ru.complitex.jedani.worker.page.catalog.*;
import ru.complitex.jedani.worker.page.login.LoginPage;
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
import ru.complitex.jedani.worker.page.worker.WorkerStructurePage;
import ru.complitex.name.page.FirstNameListPage;
import ru.complitex.name.page.LastNameListPage;
import ru.complitex.name.page.MiddleNameListPage;

/**
 * @author Anatoly A. Ivanov
 * 21.11.2017 15:04
 */
public class JedaniWebApplication extends WebApplication{
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    @Override
    protected void init() {
        new CdiConfiguration().setPropagation(ConversationPropagation.ALL).configure(this);

        configureBootstrap();
        configureMountPage();
        configureMountResource();

        getDebugSettings().setAjaxDebugModeEnabled(false);

        getSecuritySettings().setAuthorizationStrategy(new ServletAuthorizationStrategy());
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(new ServletUnauthorizedListener(LoginPage.class));
    }

    private void configureBootstrap() {
        IBootstrapSettings settings = new BootstrapSettings();
        settings.setThemeProvider(new SingleThemeProvider(new GoogleTheme()));

        Bootstrap.builder().withBootstrapSettings(settings).install(this);
    }

    private void configureMountPage() {
        mountPage("login", LoginPage.class);
        mountPage("import", ImportPage.class);
        mountPage("countries", CountryListPage.class);
        mountPage("regions", RegionListPage.class);
        mountPage("cities", CityListPage.class);
        mountPage("workers", WorkerListPage.class);
        mountPage("worker", WorkerPage.class);
        mountPage("worker/${id}", WorkerPage.class);
        mountPage("worker/structure/${id}", WorkerStructurePage.class);
        mountPage("graph", GraphPage.class);
        mountPage("mk_statuses", MkStatusListPage.class);
        mountPage("positions", PositionListPage.class);
        mountPage("last_names", LastNameListPage.class);
        mountPage("middle_names", MiddleNameListPage.class);
        mountPage("first_names", FirstNameListPage.class);
        mountPage("nomenclatures", NomenclatureListPage.class);
        mountPage("storages", StorageListPage.class);
        mountPage("storage", StoragePage.class);
        mountPage("storage/${id}", StoragePage.class);
        mountPage("promotions", PromotionListPage.class);
        mountPage("sales", SaleListPage.class);
        mountPage("currencies", CurrencyListPage.class);
        mountPage("exchange_rates", ExchangeRateListPage.class);
        mountPage("exchange_rate/${id}", ExchangeRatePage.class);
    }

    private void configureMountResource(){
        mountResource("js/jquery.js", JQueryResourceReference.getV2());
        mountResource("js/wicket-ajax-jquery.js", WicketAjaxJQueryResourceReference.get());

        mountResource("css/jedani.css", JedaniCssResourceReference.INSTANCE);
        mountResource("js/jedani.js", JedaniJsResourceReference.INSTANCE);

        mountResource("css/menu.css", MenuCssResourceReference.INSTANCE);
        mountResource("js/menu.js", MenuJsResourceReference.INSTANCE);

        mountResource("css/bootstrap.css", BootstrapCssReference.instance());
        mountResource("css/todc-bootstrap.css", GoogleCssReference.instance());
        mountResource("img/checkmark.png", new SharedResourceReference(GoogleCssReference.class,
                "img/checkmark.png"));

        mountResource("fonts/glyphicons-halflings-regular.woff2", new WebjarsCssResourceReference(
                "/bootstrap/current/fonts/glyphicons-halflings-regular.woff2"));
        mountResource("fonts/glyphicons-halflings-regular.woff", new WebjarsCssResourceReference(
                "/bootstrap/current/fonts/glyphicons-halflings-regular.woff"));
        mountResource("fonts/glyphicons-halflings-regular.ttf", new WebjarsCssResourceReference(
                "/bootstrap/current/fonts/glyphicons-halflings-regular.ttf"));

        mountResource("js/jquery-migrate.js", JQueryMigrateJavaScriptReference.instance());
        mountResource("js/bootstrap.js", BootstrapJavaScriptReference.instance());

        mountResource("css/datepicker3.css", BootstrapDatepickerReference.INSTANCE);
        mountResource("js/datepicker.js", BootstrapDatepickerJsReference.INSTANCE);
        mountResource("js/lang/bootstrap-datepicker.ru.js", new BootstrapDatepickerLangJsReference("ru"));

        mountResource("js/bootstrap-select.js", SelectJSReference.instance());
        mountResource("css/bootstrap-select.css", SelectCSSReference.instance());

        mountResource("js/wicket-autocomplete.js", AutoCompleteBehavior.AUTOCOMPLETE_JS);
    }

    @Override
    public Session newSession(Request request, Response response) {
        if (request instanceof ServletWebRequest){
            return new ServletWebSession((ServletWebRequest) request);
        }

        return super.newSession(request, response);
    }
}
