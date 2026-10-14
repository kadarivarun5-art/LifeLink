package com.lifelink.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import java.io.IOException;

/** Ends the authenticated session. Logout is POST-only to prevent cross-site logout requests. */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Allow", "POST");
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        Cookie remember = new Cookie("rememberedEmail", "");
        remember.setMaxAge(0);
        remember.setHttpOnly(true);
        remember.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        resp.addCookie(remember);
        resp.sendRedirect(req.getContextPath() + "/login?loggedout=1");
    }
}
