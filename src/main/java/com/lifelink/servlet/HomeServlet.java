package com.lifelink.servlet;

import com.lifelink.dao.RequestDAO;
import com.lifelink.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import com.lifelink.util.AppConstants;

/**
 * Controller (the "C" in MVC) for the home page.
 * It asks the Model (DAOs) for data, stores it in the request, and forwards to the View (home.jsp).
 */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final RequestDAO requestDAO = new RequestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("donorCount", userDAO.countAvailableDonors());
            req.setAttribute("openRequestCount", requestDAO.countOpen());
            req.setAttribute("latestRequests", requestDAO.findOpen(null, null, 3));
        } catch (SQLException e) {
            throw new ServletException("Could not load home page data", e);
        }
        req.setAttribute("bloodGroups", AppConstants.BLOOD_GROUPS);
        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }
}
