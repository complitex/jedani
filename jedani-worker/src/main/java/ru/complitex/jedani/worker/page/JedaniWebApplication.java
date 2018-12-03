package ru.complitex.jedani.worker.page;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.core.settings.SingleThemeProvider;
import de.agilecoders.wicket.themes.markup.html.google.GoogleTheme;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.cdi.ConversationPropagation;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import ru.complitex.address.page.*;
import ru.complitex.common.wicket.application.ServletAuthorizationStrategy;
import ru.complitex.common.wicket.application.ServletUnauthorizedListener;
import ru.complitex.common.wicket.application.ServletWebSession;
import ru.complitex.jedani.worker.graph.GraphPage;
import ru.complitex.jedani.worker.page.admin.ImportPage;
import ru.complitex.jedani.worker.page.catalog.MkStatusEditPage;
import ru.complitex.jedani.worker.page.catalog.MkStatusListPage;
import ru.complitex.jedani.worker.page.catalog.PositionEditPage;
import ru.complitex.jedani.worker.page.catalog.PositionListPage;
import ru.complitex.jedani.worker.page.login.LoginPage;
import ru.complitex.jedani.worker.page.storage.NomenclatureEditPage;
import ru.complitex.jedani.worker.page.storage.NomenclatureListPage;
import ru.complitex.jedani.worker.page.storage.StorageListPage;
import ru.complitex.jedani.worker.page.storage.StoragePage;
import ru.complitex.jedani.worker.page.worker.WorkerListPage;
import ru.complitex.jedani.worker.page.worker.WorkerPage;
import ru.complitex.jedani.worker.page.worker.WorkerStructurePage;
import ru.complitex.name.page.*;

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
        configureMount();

        getDebugSettings().setAjaxDebugModeEnabled(false);

        getSecuritySettings().setAuthorizationStrategy(new ServletAuthorizationStrategy());
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(new ServletUnauthorizedListener(LoginPage.class));
    }

    private void configureBootstrap() {
        IBootstrapSettings settings = new BootstrapSettings();
        Bootstrap.builder().withBootstrapSettings(settings).install(this);

        settings.setThemeProvider(new SingleThemeProvider(new GoogleTheme()));

        Bootstrap.install(this);
    }

    private void configureMount() {
        mountPage("login", LoginPage.class);
        mountPage("import", ImportPage.class);
        mountPage("countries", CountryListPage.class);
        mountPage("country", CountryEditPage.class);
        mountPage("country/${id}", CountryEditPage.class);
        mountPage("regions", RegionListPage.class);
        mountPage("region", RegionEditPage.class);
        mountPage("region/${id}", RegionEditPage.class);
        mountPage("cities", CityListPage.class);
        mountPage("city", CityEditPage.class);
        mountPage("city/${id}", CityEditPage.class);
        mountPage("workers", WorkerListPage.class);
        mountPage("worker", WorkerPage.class);
        mountPage("worker/${id}", WorkerPage.class);
        mountPage("worker/structure/${id}", WorkerStructurePage.class);
        mountPage("graph", GraphPage.class);
        mountPage("mk_statuses", MkStatusListPage.class);
        mountPage("mk_status", MkStatusEditPage.class);
        mountPage("mk_status/${id}", MkStatusEditPage.class);
        mountPage("positions", PositionListPage.class);
        mountPage("position", PositionEditPage.class);
        mountPage("position/${id}", PositionEditPage.class);
        mountPage("last_names", LastNameListPage.class);
        mountPage("last_name", LastNameEditPage.class);
        mountPage("last_name/${id}", LastNameEditPage.class);
        mountPage("middle_names", MiddleNameListPage.class);
        mountPage("middle_name", MiddleNameEditPage.class);
        mountPage("middle_name/${id}", MiddleNameEditPage.class);
        mountPage("first_names", FirstNameListPage.class);
        mountPage("first_name", FirstNameEditPage.class);
        mountPage("first_name/${id}", FirstNameEditPage.class);
        mountPage("nomenclatures", NomenclatureListPage.class);
        mountPage("nomenclature", NomenclatureEditPage.class);
        mountPage("nomenclature/${id}", NomenclatureEditPage.class);
        mountPage("storages", StorageListPage.class);
        mountPage("storage", StoragePage.class);
        mountPage("storage/${id}", StoragePage.class);
    }

    @Override
    public Session newSession(Request request, Response response) {
        if (request instanceof ServletWebRequest){
            return new ServletWebSession((ServletWebRequest) request);
        }

        return super.newSession(request, response);
    }
}
