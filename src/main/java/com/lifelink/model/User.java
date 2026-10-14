package com.lifelink.model;

import java.io.Serializable;

/**
 * JavaBean for a registered user (who is also a blood donor).
 * Rules of a bean: public no-arg constructor + private fields + getters/setters.
 * JSP pages access it with jsp:useBean and jsp:getProperty.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String fullName;
    private String email;
    private String phone;
    private String bloodGroup;
    private String city;
    private boolean available;
    // Phone numbers are hidden from donor search unless the donor opts in.
    private boolean phoneVisible;

    public User() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public boolean isPhoneVisible() { return phoneVisible; }
    public void setPhoneVisible(boolean phoneVisible) { this.phoneVisible = phoneVisible; }
}
