package ru.complitex.jedani.web;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.core.settings.SingleThemeProvider;
import de.agilecoders.wicket.less.BootstrapLess;
import de.agilecoders.wicket.themes.markup.html.google.GoogleTheme;
import org.apache.wicket.Page;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.protocol.http.WebApplication;
import ru.complitex.jedani.web.catalog.ImportPage;
import ru.complitex.jedani.web.user.UserListPage;

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
    }

    private void configureBootstrap() {
        IBootstrapSettings settings = new BootstrapSettings();
        Bootstrap.builder().withBootstrapSettings(settings).install(this);

        settings.setThemeProvider(new SingleThemeProvider(new GoogleTheme()));

        BootstrapLess.install(this);
    }

    private void configureMount() {
        mountPage("import", ImportPage.class);
        mountPage("users", UserListPage.class);

    }
}
