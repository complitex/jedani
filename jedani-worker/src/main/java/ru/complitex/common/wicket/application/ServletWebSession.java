package ru.complitex.common.wicket.application;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;

import javax.servlet.ServletException;

/**
 * @author Anatoly A. Ivanov
 * 29.12.2017 19:49
 */
public class ServletWebSession extends WebSession{
    private ServletWebRequest request;

    public ServletWebSession(ServletWebRequest request) {
        super(request);

        this.request = request;
    }

    @Override
    public void invalidate() {
        super.invalidate();

        try {
            request.getContainerRequest().logout();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
