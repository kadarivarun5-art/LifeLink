package com.lifelink.servlet;

import com.lifelink.dao.UserDAO;
import com.lifelink.model.User;
import com.lifelink.util.AppConstants;
import com.lifelink.util.EmailService;
import com.lifelink.util.VerificationTokenUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import javax.mail.MessagingException;
import java.util.regex.Pattern;

/** Sign-up as a donor: validates the form, saves the user, then redirects to the login page. */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User u = new User();
        u.setFullName(value(req, "fullName"));
        u.setEmail(value(req, "email"));
        u.setPhone(value(req, "phone").replaceAll("[\\s-]", ""));
        u.setBloodGroup(value(req, "bloodGroup"));
        u.setCity(value(req, "city"));
        u.setAvailable(req.getParameter("available") != null);
        u.setPhoneVisible(req.getParameter("sharePhone") != null);
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");

        String error = null;
        if (u.getFullName().length() < 2 || u.getFullName().length() > 100) {
            error = "Please enter your name (2 to 100 characters).";
        } else if (!EMAIL.matcher(u.getEmail()).matches() || u.getEmail().length() > 150) {
            error = "Please enter a valid e-mail address.";
        } else if (!AppConstants.PHONE.matcher(u.getPhone()).matches()) {
            error = "Please enter a valid phone number (10 to 13 digits).";
        } else if (!AppConstants.BLOOD_GROUPS.contains(u.getBloodGroup())) {
            error = "Please choose your blood group.";
        } else if (u.getCity().length() < 2 || u.getCity().length() > 60) {
            error = "Please enter your city.";
        } else if (!"on".equals(req.getParameter("agree"))) {
            error = "Please confirm that you are 18 or older and willing to be contacted.";
        } else if (password == null || password.length() < 10) {
            error = "Password must be at least 10 characters.";
        } else if (!password.equals(confirm)) {
            error = "The two passwords do not match.";
        }

        try {
            if (error == null && !EmailService.isConfigured()) {
                error = "Email verification is not configured yet. Please contact the site administrator before registering.";
            }
            if (error == null && userDAO.emailExists(u.getEmail())) {
                error = "This e-mail is already registered. Try logging in.";
            }
            if (error == null) {
                String token = VerificationTokenUtil.newToken();
                String tokenHash = VerificationTokenUtil.hash(token);
                Timestamp expires = Timestamp.from(Instant.now().plusSeconds(24 * 60 * 60));
                userDAO.register(u, password, tokenHash, expires);
                String baseUrl = req.getScheme() + "://" + req.getServerName()
                        + ((req.getServerPort() == 80 && "http".equals(req.getScheme()))
                        || (req.getServerPort() == 443 && "https".equals(req.getScheme()))
                        ? "" : ":" + req.getServerPort()) + req.getContextPath();
                try {
                    EmailService.sendVerificationEmail(u.getEmail(), baseUrl + "/verify-email?token=" + token);
                } catch (MessagingException | RuntimeException mailError) {
                    getServletContext().log("Could not send verification email", mailError);
                    try { userDAO.deleteUnverifiedAccount(u.getEmail()); } catch (SQLException cleanupError) { mailError.addSuppressed(cleanupError); }
                    req.setAttribute("error", "The verification email could not be sent. Check your details and try again later, or contact the site administrator.");
                    forward(req, resp);
                    return;
                }
                resp.sendRedirect(req.getContextPath() + "/login?verifySent=1");
                return;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error while registering", e);
        }

        req.setAttribute("error", error);
        forward(req, resp);
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("bloodGroups", AppConstants.BLOOD_GROUPS);
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    private static String value(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
}
