package ru.complitex.common.wicket.application;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import ru.complitex.domain.util.Locales;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Anatoly A. Ivanov
 * 29.12.2017 19:49
 */
public class ServletWebSession extends WebSession{
    public ServletWebSession(ServletWebRequest request) {
        super(request);

        setLocale(Locales.RU);
    }

    @Override
    public void invalidate() {
        super.invalidate();

        try {
            ((HttpServletRequest)RequestCycle.get().getRequest().getContainerRequest()).logout();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
