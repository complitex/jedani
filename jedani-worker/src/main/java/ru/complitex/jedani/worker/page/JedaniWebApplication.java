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
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.spinner.Spinner;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.references.BootstrapDatepickerJsReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.references.BootstrapDatepickerLangJsReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.references.BootstrapDatepickerReference;
import de.agilecoders.wicket.themes.markup.html.google.GoogleCssReference;
import de.agilecoders.wicket.themes.markup.html.google.GoogleTheme;
import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.WicketAjaxJQueryResourceReference;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.cdi.ConversationPropagation;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteBehavior;
import org.apache.wicket.markup.html.pages.AccessDeniedPage;
import org.apache.wicket.markup.html.pages.InternalErrorPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;
import ru.complitex.address.page.CityListPage;
import ru.complitex.address.page.CountryListPage;
import ru.complitex.address.page.RegionListPage;
import ru.complitex.common.wicket.application.ServletAuthorizationStrategy;
import ru.complitex.common.wicket.application.ServletUnauthorizedListener;
import ru.complitex.common.wicket.application.ServletWebSession;
import ru.complitex.common.wicket.converter.BigDecimalConverter;
import ru.complitex.jedani.worker.page.admin.ImportPage;
import ru.complitex.jedani.worker.page.admin.SettingPage;
import ru.complitex.jedani.worker.page.card.CardListPage;
import ru.complitex.jedani.worker.page.catalog.*;
import ru.complitex.jedani.worker.page.login.LoginPage;
import ru.complitex.jedani.worker.page.price.PriceListPage;
import ru.complitex.jedani.worker.page.promotion.PromotionListPage;
import ru.complitex.jedani.worker.page.resource.*;
import ru.complitex.jedani.worker.page.sale.SaleDecisionListPage;
import ru.complitex.jedani.worker.page.sale.SaleListPage;
import ru.complitex.jedani.worker.page.storage.NomenclatureListPage;
import ru.complitex.jedani.worker.page.storage.StorageListPage;
import ru.complitex.jedani.worker.page.storage.StoragePage;
import ru.complitex.jedani.worker.page.worker.RegionalLeaderPage;
import ru.complitex.jedani.worker.page.worker.WorkerListPage;
import ru.complitex.jedani.worker.page.worker.WorkerPage;
import ru.complitex.jedani.worker.page.worker.WorkerStructurePage;
import ru.complitex.name.page.FirstNameListPage;
import ru.complitex.name.page.LastNameListPage;
import ru.complitex.name.page.MiddleNameListPage;

import java.math.BigDecimal;

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

        ((ConverterLocator)getConverterLocator()).set(BigDecimal.class, new BigDecimalConverter());
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
        mountPage("cards", CardListPage.class);
        mountPage("regional-leader", RegionalLeaderPage.class);
        mountPage("regional-leader/${id}", RegionalLeaderPage.class);
        mountPage("worker/structure/${id}", WorkerStructurePage.class);
        mountPage("mk-statuses", MkStatusListPage.class);
        mountPage("positions", PositionListPage.class);
        mountPage("last-names", LastNameListPage.class);
        mountPage("middle-names", MiddleNameListPage.class);
        mountPage("first-names", FirstNameListPage.class);
        mountPage("nomenclatures", NomenclatureListPage.class);
        mountPage("storages", StorageListPage.class);
        mountPage("storage", StoragePage.class);
        mountPage("storage/${id}", StoragePage.class);
        mountPage("promotions", PromotionListPage.class);
        mountPage("sales", SaleListPage.class);
        mountPage("sale-decisions", SaleDecisionListPage.class);
        mountPage("currencies", CurrencyListPage.class);
        mountPage("exchange-rates", ExchangeRateListPage.class);
        mountPage("exchange-rate/${id}", ExchangeRatePage.class);
        mountPage("prices", PriceListPage.class);
        mountPage("settings", SettingPage.class);

        mountPage("error", InternalErrorPage.class);
        mountPage("access-denied", AccessDeniedPage.class);
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

        mountResource("img/jedani.svg", JedaniLogoImgResourceReference.INSTANCE);
        mountResource("favicon.ico", JedaniFaviconResourceReference.INSTANCE);

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

        mountResource("css/touchspin.min.css", new CssResourceReference(Spinner.class,"css/touchspin.min.css"));
        mountResource("js/touchspin.min.js", new JavaScriptResourceReference(Spinner.class,"js/touchspin.min.js"));
    }

    @Override
    public Session newSession(Request request, Response response) {
        if (request instanceof ServletWebRequest){
            return new ServletWebSession((ServletWebRequest) request);
        }

        return super.newSession(request, response);
    }
}
