package ru.complitex.common.wicket.application;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Anatoly A. Ivanov
 * 27.03.2020 12:25 AM
 */
public class SessionFilter implements Filter {
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
