package com.xsslab.hrportal.model;

import java.sql.Timestamp;

public class EmployeeBio {
    private int id;
    private String employeeName;
    private String department;
    private String bio;
    private String hobbies;
    private Timestamp createdAt;

    public EmployeeBio() {}

    public EmployeeBio(String employeeName, String department, String bio, String hobbies) {
        this.employeeName = employeeName;
        this.department = department;
        this.bio = bio;
        this.hobbies = hobbies;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getHobbies() { return hobbies; }
    public void setHobbies(String hobbies) { this.hobbies = hobbies; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
