package com.xsslab.hrportal.model;

import java.sql.Timestamp;

public class JobPosting {
    private int id;
    private String title;
    private String department;
    private String description;
    private String requirements;
    private Timestamp createdAt;

    public JobPosting() {}

    public JobPosting(String title, String department, String description, String requirements) {
        this.title = title;
        this.department = department;
        this.description = description;
        this.requirements = requirements;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
