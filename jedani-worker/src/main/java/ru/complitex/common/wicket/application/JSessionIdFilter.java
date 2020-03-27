package ru.complitex.common.wicket.application;

import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.string.StringValue;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Anatoly A. Ivanov
 * 27.03.2020 12:25 AM
 */
public class JSessionIdFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jsessionid = request.getParameter("jsessionid");

        if (jsessionid != null){
            HttpServletResponse httpServletResponse = ((HttpServletResponse)response);

            httpServletResponse.addCookie(new Cookie("jsessionid", jsessionid));

            httpServletResponse.sendRedirect(((HttpServletRequest) request).getRequestURI());
        }else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
