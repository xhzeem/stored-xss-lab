package com.xsslab.hrportal.model;

import java.sql.Timestamp;

public class OnboardingTask {
    private int id;
    private String employeeName;
    private String taskName;
    private String notes;
    private boolean completed;
    private Timestamp createdAt;

    public OnboardingTask() {}

    public OnboardingTask(String employeeName, String taskName, String notes, boolean completed) {
        this.employeeName = employeeName;
        this.taskName = taskName;
        this.notes = notes;
        this.completed = completed;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
