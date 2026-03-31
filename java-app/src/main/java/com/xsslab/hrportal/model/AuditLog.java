package com.xsslab.hrportal.model;

import java.sql.Timestamp;

public class AuditLog {
    private int id;
    private String actor;
    private String action;
    private String details;
    private Timestamp createdAt;

    public AuditLog() {}

    public AuditLog(String actor, String action, String details) {
        this.actor = actor;
        this.action = action;
        this.details = details;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
