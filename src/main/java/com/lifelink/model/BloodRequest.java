package com.lifelink.model;

import java.io.Serializable;
import java.sql.Date;

/** JavaBean for an urgent blood request posted by a user. */
public class BloodRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long userId;
    private String patientName;
    private String bloodGroup;
    private int units;
    private String hospital;
    private String city;
    private String contactPhone;
    private Date neededBy;
    private String note;
    private String status;

    public BloodRequest() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }

    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public Date getNeededBy() { return neededBy; }
    public void setNeededBy(Date neededBy) { this.neededBy = neededBy; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
