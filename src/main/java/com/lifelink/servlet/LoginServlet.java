package com.lifelink.servlet;

import com.lifelink.dao.UserDAO;
import com.lifelink.model.User;
import com.lifelink.util.AppConstants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/** Login: handles the HTTP request/response, session creation and the "remember me" cookie. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final int THIRTY_DAYS = 60 * 60 * 24 * 30;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Cookie management (read): pre-fill the e-mail if the user ticked "remember me" earlier
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (AppConstants.REMEMBER_COOKIE.equals(c.getName())) {
                    req.setAttribute("rememberedEmail", c.getValue());
                }
            }
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = trim(req.getParameter("email"));
        String password = req.getParameter("password");
        boolean remember = req.getParameter("remember") != null;

        User user;
        try {
            user = (email.isEmpty() || password == null) ? null : userDAO.authenticate(email, password);
        } catch (SQLException e) {
            throw new ServletException("Database error while logging in", e);
        }

        if (user == null) {
            req.setAttribute("error", "Wrong e-mail or password. Please try again.");
            req.setAttribute("rememberedEmail", email);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        // Session management: start a fresh session (prevents session fixation)
        HttpSession old = req.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        HttpSession session = req.getSession(true);
        session.setAttribute(AppConstants.SESSION_USER, user);
        session.setMaxInactiveInterval(30 * 60); // 30 minutes

        // Cookie management (write / delete)
        Cookie cookie = new Cookie(AppConstants.REMEMBER_COOKIE, remember ? user.getEmail() : "");
        cookie.setMaxAge(remember ? THIRTY_DAYS : 0);
        cookie.setHttpOnly(true);
        cookie.setSecure(req.isSecure());
        cookie.setPath("/");
        resp.addCookie(cookie);

        // Redirection
        resp.sendRedirect(req.getContextPath() + "/home");
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
