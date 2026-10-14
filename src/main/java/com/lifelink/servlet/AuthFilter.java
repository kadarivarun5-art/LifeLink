package com.lifelink.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.lifelink.util.AppConstants;
import java.io.IOException;

/** Session management: only logged-in users may open these pages. */
@WebFilter(urlPatterns = {"/profile", "/request/new"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(AppConstants.SESSION_USER) == null) {
            boolean expired = req.getRequestedSessionId() != null && !req.isRequestedSessionIdValid();
            // Redirection
            res.sendRedirect(req.getContextPath() + "/login" + (expired ? "?timeout=1" : "?needlogin=1"));
            return;
        }
        // Stop the browser "Back" button from showing private pages after logout
        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        res.setHeader("Pragma", "no-cache");
        chain.doFilter(request, response);
    }
}
