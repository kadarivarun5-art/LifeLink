package com.lifelink.servlet;

import com.lifelink.dao.RequestDAO;
import com.lifelink.util.AppConstants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/** Public list of open blood requests, with optional filters. */
@WebServlet("/requests")
public class RequestServlet extends HttpServlet {
    private final RequestDAO requestDAO = new RequestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String group = req.getParameter("group");
        if (group != null && !AppConstants.BLOOD_GROUPS.contains(group)) {
            group = null;
        }
        String city = req.getParameter("city");
        city = (city == null || city.trim().isEmpty()) ? null : city.trim();
        if (city != null && city.length() > 60) {
            city = city.substring(0, 60);
        }
        try {
            req.setAttribute("requests", requestDAO.findOpen(group, city, 0));
        } catch (SQLException e) {
            throw new ServletException("Could not load requests", e);
        }
        req.setAttribute("bloodGroups", AppConstants.BLOOD_GROUPS);
        req.setAttribute("selectedGroup", group);
        req.setAttribute("selectedCity", city);
        req.getRequestDispatcher("/WEB-INF/views/requests.jsp").forward(req, resp);
    }
}
