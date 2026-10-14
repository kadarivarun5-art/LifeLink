package com.lifelink.servlet;

import com.lifelink.dao.RequestDAO;
import com.lifelink.dao.UserDAO;
import com.lifelink.model.User;
import com.lifelink.util.AppConstants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * My profile page: update donor details, switch availability on/off,
 * and manage (close or delete) my own blood requests. Login required (AuthFilter).
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final RequestDAO requestDAO = new RequestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        showPage(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = (User) req.getSession().getAttribute(AppConstants.SESSION_USER);
        String action = req.getParameter("action");

        try {
            if ("update".equals(action)) {
                String phone = param(req, "phone").replaceAll("[\\s-]", "");
                String group = param(req, "bloodGroup");
                String city = param(req, "city");

                String error = null;
                if (!AppConstants.PHONE.matcher(phone).matches()) {
                    error = "Please enter a valid phone number (10 to 13 digits).";
                } else if (!AppConstants.BLOOD_GROUPS.contains(group)) {
                    error = "Please choose your blood group.";
                } else if (city.length() < 2 || city.length() > 60) {
                    error = "Please enter your city.";
                }
                if (error != null) {
                    req.setAttribute("error", error);
                    showPage(req, resp);
                    return;
                }
                user.setPhone(phone);
                user.setBloodGroup(group);
                user.setCity(city);
                user.setAvailable(req.getParameter("available") != null);
                user.setPhoneVisible(req.getParameter("sharePhone") != null);
                userDAO.updateProfile(user);
                req.getSession().setAttribute(AppConstants.SESSION_USER, user);
                resp.sendRedirect(req.getContextPath() + "/profile?msg=updated");
                return;
            }

            long id = parseId(req.getParameter("id"));
            if ("fulfil".equals(action)) {
                requestDAO.markFulfilled(id, user.getId());
                resp.sendRedirect(req.getContextPath() + "/profile?msg=closed");
                return;
            }
            if ("delete".equals(action)) {
                requestDAO.delete(id, user.getId());
                resp.sendRedirect(req.getContextPath() + "/profile?msg=deleted");
                return;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
        resp.sendRedirect(req.getContextPath() + "/profile");
    }

    private void showPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute(AppConstants.SESSION_USER);
        try {
            req.setAttribute("myRequests", requestDAO.findByUser(user.getId()));
        } catch (SQLException e) {
            throw new ServletException("Could not load your requests", e);
        }
        req.setAttribute("bloodGroups", AppConstants.BLOOD_GROUPS);
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(req, resp);
    }

    private static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }

    private static long parseId(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
