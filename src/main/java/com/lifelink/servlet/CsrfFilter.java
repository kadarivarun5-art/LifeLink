package com.lifelink.servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Synchronizer-token CSRF protection for every state-changing POST request. */
@WebFilter("/*")
public class CsrfFilter implements Filter {
    public static final String TOKEN_ATTRIBUTE = "csrfToken";
    private static final String SESSION_KEY = CsrfFilter.class.getName() + ".token";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(req.getMethod().equalsIgnoreCase("GET"));
        if (session != null) {
            String token = (String) session.getAttribute(SESSION_KEY);
            if (token == null) {
                byte[] bytes = new byte[32];
                RANDOM.nextBytes(bytes);
                token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                session.setAttribute(SESSION_KEY, token);
            }
            req.setAttribute(TOKEN_ATTRIBUTE, token);
        }
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String expected = session == null ? null : (String) session.getAttribute(SESSION_KEY);
            String supplied = req.getParameter(TOKEN_ATTRIBUTE);
            if (expected == null || supplied == null ||
                    !MessageDigest.isEqual(expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                                           supplied.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing security token. Reload the page and try again.");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
