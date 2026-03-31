package com.xsslab.hrportal.model;

import java.sql.Timestamp;

public class Applicant {
    private int id;
    private String name;
    private String email;
    private String position;
    private String summary;
    private String status;
    private Timestamp createdAt;

    public Applicant() {}

    public Applicant(String name, String email, String position, String summary, String status) {
        this.name = name;
        this.email = email;
        this.position = position;
        this.summary = summary;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
