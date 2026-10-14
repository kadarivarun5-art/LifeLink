package com.lifelink.servlet;

import com.lifelink.dao.UserDAO;
import com.lifelink.util.AppConstants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Public donor search: /donors?group=O%2B&city=Hyderabad
 * Reading request parameters with getParameter().
 */
@WebServlet("/donors")
public class DonorServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

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
            req.setAttribute("donors", userDAO.findDonors(group, city));
        } catch (SQLException e) {
            throw new ServletException("Could not search donors", e);
        }
        req.setAttribute("bloodGroups", AppConstants.BLOOD_GROUPS);
        req.setAttribute("selectedGroup", group);
        req.setAttribute("selectedCity", city);
        req.getRequestDispatcher("/WEB-INF/views/donors.jsp").forward(req, resp);
    }
}
