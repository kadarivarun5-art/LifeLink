package com.lifelink.servlet;

import com.lifelink.dao.RequestDAO;
import com.lifelink.model.BloodRequest;
import com.lifelink.model.User;
import com.lifelink.util.AppConstants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Post a new blood request (login required - see AuthFilter).
 * The maximum number of units comes from a servlet init parameter.
 */
@WebServlet(urlPatterns = "/request/new",
        initParams = @WebInitParam(name = "maxUnits", value = "10"))
public class NewRequestServlet extends HttpServlet {
    private final RequestDAO requestDAO = new RequestDAO();
    private int maxUnits;

    /** Servlet life cycle: init() runs once when the servlet is first loaded. */
    @Override
    public void init() throws ServletException {
        maxUnits = Integer.parseInt(getInitParameter("maxUnits"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        showForm(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = (User) req.getSession().getAttribute(AppConstants.SESSION_USER);

        BloodRequest r = new BloodRequest();
        r.setUserId(user.getId());
        r.setPatientName(value(req, "patientName"));
        r.setBloodGroup(value(req, "bloodGroup"));
        r.setHospital(value(req, "hospital"));
        r.setCity(value(req, "city"));
        r.setContactPhone(value(req, "contactPhone").replaceAll("[\\s-]", ""));
        r.setNote(value(req, "note"));

        String error = null;
        int units = 0;
        Date neededBy = null;
        try {
            units = Integer.parseInt(value(req, "units"));
        } catch (NumberFormatException ignored) {
            // handled below
        }
        try {
            LocalDate d = LocalDate.parse(value(req, "neededBy"));
            if (d.isBefore(LocalDate.now())) {
                error = "The needed-by date cannot be in the past.";
            }
            neededBy = Date.valueOf(d);
        } catch (DateTimeParseException ex) {
            error = "Please choose a valid needed-by date.";
        }

        if (r.getPatientName().isEmpty() || r.getPatientName().length() > 100) {
            error = "Please enter the patient name.";
        } else if (!AppConstants.BLOOD_GROUPS.contains(r.getBloodGroup())) {
            error = "Please choose the blood group needed.";
        } else if (units < 1 || units > maxUnits) {
            error = "Units must be between 1 and " + maxUnits + ".";
        } else if (r.getHospital().isEmpty() || r.getHospital().length() > 150) {
            error = "Please enter the hospital name.";
        } else if (r.getCity().isEmpty() || r.getCity().length() > 60) {
            error = "Please enter the city.";
        } else if (!AppConstants.PHONE.matcher(r.getContactPhone()).matches()) {
            error = "Please enter a valid contact number (10 to 13 digits).";
        } else if (r.getNote().length() > 255) {
            error = "Note is too long (maximum 255 characters).";
        }

        if (error == null) {
            r.setUnits(units);
            r.setNeededBy(neededBy);
            try {
                requestDAO.add(r);
            } catch (SQLException e) {
                throw new ServletException("Could not save the request", e);
            }
            resp.sendRedirect(req.getContextPath() + "/requests?msg=posted");
            return;
        }
        req.setAttribute("error", error);
        showForm(req, resp);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("bloodGroups", AppConstants.BLOOD_GROUPS);
        req.setAttribute("today", LocalDate.now().toString());
        req.setAttribute("maxUnits", maxUnits);
        req.getRequestDispatcher("/WEB-INF/views/request-new.jsp").forward(req, resp);
    }

    private static String value(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
}
