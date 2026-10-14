package com.lifelink.dao;

import com.lifelink.model.BloodRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Data Access Object for blood requests. */
public class RequestDAO {

    private static final String COLUMNS = "id, user_id, patient_name, blood_group, units, "
            + "hospital, city, contact_phone, needed_by, note, status";

    public void add(BloodRequest r) throws SQLException {
        String sql = "INSERT INTO blood_requests(user_id, patient_name, blood_group, units, "
                + "hospital, city, contact_phone, needed_by, note) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, r.getUserId());
            ps.setString(2, r.getPatientName());
            ps.setString(3, r.getBloodGroup());
            ps.setInt(4, r.getUnits());
            ps.setString(5, r.getHospital());
            ps.setString(6, r.getCity());
            ps.setString(7, r.getContactPhone());
            ps.setDate(8, r.getNeededBy());
            ps.setString(9, r.getNote());
            ps.executeUpdate();
        }
    }

    /** Open requests that are not yet past their needed-by date. limit == 0 means no limit. */
    public List<BloodRequest> findOpen(String bloodGroup, String city, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNS
                + " FROM blood_requests WHERE status = 'OPEN' AND needed_by >= CURRENT_DATE");
        if (bloodGroup != null) {
            sql.append(" AND blood_group = ?");
        }
        if (city != null) {
            sql.append(" AND LOWER(city) LIKE ?");
        }
        sql.append(" ORDER BY needed_by ASC, id DESC");
        if (limit > 0) {
            sql.append(" LIMIT ").append(limit);
        }
        List<BloodRequest> list = new ArrayList<>();
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

    public List<BloodRequest> findByUser(long userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM blood_requests WHERE user_id = ? ORDER BY id DESC";
        List<BloodRequest> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public int countOpen() throws SQLException {
        String sql = "SELECT COUNT(*) FROM blood_requests WHERE status = 'OPEN' AND needed_by >= CURRENT_DATE";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Marks the request as fulfilled. Only the owner can do this. */
    public boolean markFulfilled(long id, long userId) throws SQLException {
        String sql = "UPDATE blood_requests SET status = 'FULFILLED' WHERE id = ? AND user_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(long id, long userId) throws SQLException {
        String sql = "DELETE FROM blood_requests WHERE id = ? AND user_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private BloodRequest map(ResultSet rs) throws SQLException {
        BloodRequest r = new BloodRequest();
        r.setId(rs.getLong("id"));
        r.setUserId(rs.getLong("user_id"));
        r.setPatientName(rs.getString("patient_name"));
        r.setBloodGroup(rs.getString("blood_group"));
        r.setUnits(rs.getInt("units"));
        r.setHospital(rs.getString("hospital"));
        r.setCity(rs.getString("city"));
        r.setContactPhone(rs.getString("contact_phone"));
        r.setNeededBy(rs.getDate("needed_by"));
        r.setNote(rs.getString("note"));
        r.setStatus(rs.getString("status"));
        return r;
    }
}
