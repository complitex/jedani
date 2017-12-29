package ru.complitex.common.wicket.session;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;

/**
 * @author Anatoly A. Ivanov
 * 29.12.2017 19:49
 */
public class AuthSession extends WebSession{
    private ServletWebRequest request;

    public AuthSession(ServletWebRequest request) {
        super(request);
    }
}
