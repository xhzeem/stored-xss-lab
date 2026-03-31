package com.xsslab.hrportal.model;

import java.sql.Timestamp;

public class InterviewNote {
    private int id;
    private int applicantId;
    private String interviewer;
    private String notes;
    private int rating;
    private Timestamp createdAt;

    public InterviewNote() {}

    public InterviewNote(int applicantId, String interviewer, String notes, int rating) {
        this.applicantId = applicantId;
        this.interviewer = interviewer;
        this.notes = notes;
        this.rating = rating;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getApplicantId() { return applicantId; }
    public void setApplicantId(int applicantId) { this.applicantId = applicantId; }
    public String getInterviewer() { return interviewer; }
    public void setInterviewer(String interviewer) { this.interviewer = interviewer; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
