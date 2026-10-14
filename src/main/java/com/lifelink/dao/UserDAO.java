package com.lifelink.dao;

import com.lifelink.model.User;
import com.lifelink.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Data Access Object for users/donors (PreparedStatement = safe from SQL injection). */
public class UserDAO {

    private static final String COLUMNS =
            "id, full_name, email, phone, blood_group, city, available, phone_visible";

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void register(User u, String password, String tokenHash, Timestamp expiresAt) throws SQLException {
        String salt = PasswordUtil.newSalt();
        String hash = PasswordUtil.hash(password, salt);
        String sql = "INSERT INTO users(full_name, email, password_hash, salt, phone, "
                + "blood_group, city, available, phone_visible, email_verified, verification_token_hash, verification_expires) "
                + "VALUES (?,?,?,?,?,?,?,?,?,FALSE,?,?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail().toLowerCase());
            ps.setString(3, hash);
            ps.setString(4, salt);
            ps.setString(5, u.getPhone());
            ps.setString(6, u.getBloodGroup());
            ps.setString(7, u.getCity());
            ps.setBoolean(8, u.isAvailable());
            ps.setBoolean(9, u.isPhoneVisible());
            ps.setString(10, tokenHash);
            ps.setTimestamp(11, expiresAt);
            ps.executeUpdate();
        }
    }

    /** Returns the user if email + password are correct, otherwise null. */
    public User authenticate(String email, String password) throws SQLException {
        String sql = "SELECT " + COLUMNS + ", password_hash, salt, email_verified FROM users WHERE email = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getBoolean("email_verified") && PasswordUtil.matches(password, rs.getString("salt"),
                        rs.getString("password_hash"))) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /** Remove a pending account if SMTP delivery fails, allowing the person to retry registration. */
    public void deleteUnverifiedAccount(String email) throws SQLException {
        String sql = "DELETE FROM users WHERE email = ? AND email_verified = FALSE";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.toLowerCase());
            ps.executeUpdate();
        }
    }

    /** Verify an e-mail once using a hashed, expiring one-time token. */
    public boolean verifyEmail(String tokenHash) throws SQLException {
        String sql = "UPDATE users SET email_verified = TRUE, verification_token_hash = NULL, "
                + "verification_expires = NULL WHERE verification_token_hash = ? "
                + "AND verification_expires > CURRENT_TIMESTAMP AND email_verified = FALSE";
        try (Connection con = DBUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            return ps.executeUpdate() == 1;
        }
    }

    public void updateProfile(User u) throws SQLException {
        String sql = "UPDATE users SET phone = ?, blood_group = ?, city = ?, available = ?, phone_visible = ? WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getPhone());
            ps.setString(2, u.getBloodGroup());
            ps.setString(3, u.getCity());
            ps.setBoolean(4, u.isAvailable());
            ps.setBoolean(5, u.isPhoneVisible());
            ps.setLong(6, u.getId());
            ps.executeUpdate();
        }
    }

    /** Available donors, filtered by blood group and/or city (both optional). */
    public List<User> findDonors(String bloodGroup, String city) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT id, full_name, email, CASE WHEN phone_visible = TRUE THEN phone ELSE NULL END AS phone, "
                + "blood_group, city, available, phone_visible FROM users WHERE available = TRUE");
        if (bloodGroup != null) {
            sql.append(" AND blood_group = ?");
        }
        if (city != null) {
            sql.append(" AND LOWER(city) LIKE ?");
        }
        sql.append(" ORDER BY city, full_name LIMIT 200");
        List<User> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            if (bloodGroup != null) {
                ps.setString(i++, bloodGroup);
            }
            if (city != null) {
                ps.setString(i, "%" + city.toLowerCase() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public int countAvailableDonors() throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM users WHERE available = TRUE");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setBloodGroup(rs.getString("blood_group"));
        u.setCity(rs.getString("city"));
        u.setAvailable(rs.getBoolean("available"));
        u.setPhoneVisible(rs.getBoolean("phone_visible"));
        return u;
    }
}
