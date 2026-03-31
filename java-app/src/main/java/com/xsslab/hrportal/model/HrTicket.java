package com.xsslab.hrportal.model;

import java.sql.Timestamp;

public class HrTicket {
    private int id;
    private String employeeName;
    private String subject;
    private String description;
    private String status;
    private Timestamp createdAt;

    public HrTicket() {}

    public HrTicket(String employeeName, String subject, String description, String status) {
        this.employeeName = employeeName;
        this.subject = subject;
        this.description = description;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
