package ru.complitex.common.wicket.application;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.request.cycle.RequestCycle;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Anatoly A. Ivanov
 * 09.04.2018 18:05
 */
public class ServletUnauthorizedListener implements IUnauthorizedComponentInstantiationListener {
    private Class<? extends Page> loginPage;

    public ServletUnauthorizedListener(Class<? extends Page> loginPage) {
        this.loginPage = loginPage;
    }

    @Override
    public void onUnauthorizedInstantiation(Component component) {
        HttpServletRequest servletRequest = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();

        if (servletRequest.getSession(false) == null) {
            Session.get().invalidateNow();

            if (component instanceof Page) {
                throw new RestartResponseAtInterceptPageException(loginPage);
            } else {
                throw new UnauthorizedInstantiationException(component.getClass());
            }
        } else {
            if (servletRequest.getUserPrincipal() == null) {
                Session.get().invalidate();

                if (component instanceof Page) {
                    throw new RestartResponseAtInterceptPageException(loginPage);
                } else {
                    throw new UnauthorizedInstantiationException(component.getClass());
                }
            } else {
                throw new UnauthorizedInstantiationException(component.getClass());
            }
        }
    }
}
