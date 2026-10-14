package com.lifelink.servlet;

import com.lifelink.dao.UserDAO;
import com.lifelink.util.VerificationTokenUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/verify-email")
public class VerifyEmailServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        if (token == null || token.length() < 30 || token.length() > 100) {
            req.setAttribute("verificationResult", "invalid");
            req.getRequestDispatcher("/WEB-INF/views/verify-email.jsp").forward(req, resp);
            return;
        }
        try {
            boolean verified = userDAO.verifyEmail(VerificationTokenUtil.hash(token));
            req.setAttribute("verificationResult", verified ? "success" : "invalid");
            req.getRequestDispatcher("/WEB-INF/views/verify-email.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Database error while verifying email", e);
        }
    }
}
