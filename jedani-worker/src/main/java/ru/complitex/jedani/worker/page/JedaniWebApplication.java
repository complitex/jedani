package ru.complitex.jedani.worker.page;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.core.settings.SingleThemeProvider;
import de.agilecoders.wicket.less.BootstrapLess;
import de.agilecoders.wicket.themes.markup.html.google.GoogleTheme;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import ru.complitex.address.page.CityEditPage;
import ru.complitex.address.page.CityListPage;
import ru.complitex.address.page.RegionEditPage;
import ru.complitex.address.page.RegionListPage;
import ru.complitex.common.wicket.application.ServletAuthorizationStrategy;
import ru.complitex.common.wicket.application.ServletWebSession;
import ru.complitex.jedani.worker.graph.GraphPage;
import ru.complitex.jedani.worker.page.admin.ImportPage;
import ru.complitex.jedani.worker.page.login.LoginPage;
import ru.complitex.jedani.worker.page.worker.WorkerListPage;
import ru.complitex.jedani.worker.page.worker.WorkerPage;

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
        new CdiConfiguration().configure(this);

        configureBootstrap();
        configureMount();

        getDebugSettings().setAjaxDebugModeEnabled(false);

        getSecuritySettings().setAuthorizationStrategy(new ServletAuthorizationStrategy());
//        getSecuritySettings().setUnauthorizedComponentInstantiationListener();
    }

    private void configureBootstrap() {
        IBootstrapSettings settings = new BootstrapSettings();
        Bootstrap.builder().withBootstrapSettings(settings).install(this);

        settings.setThemeProvider(new SingleThemeProvider(new GoogleTheme()));

        BootstrapLess.install(this);
    }

    private void configureMount() {
        mountPage("login", LoginPage.class);
        mountPage("import", ImportPage.class);
        mountPage("regions", RegionListPage.class);
        mountPage("region/${id}", RegionEditPage.class);
        mountPage("cities", CityListPage.class);
        mountPage("city/${id}", CityEditPage.class);
        mountPage("workers", WorkerListPage.class);
        mountPage("worker", WorkerPage.class);
        mountPage("worker/${id}", WorkerPage.class);
        mountPage("graph", GraphPage.class);
    }

    @Override
    public Session newSession(Request request, Response response) {
        if (request instanceof ServletWebRequest){
            return new ServletWebSession((ServletWebRequest) request);
        }

        return super.newSession(request, response);
    }
}
