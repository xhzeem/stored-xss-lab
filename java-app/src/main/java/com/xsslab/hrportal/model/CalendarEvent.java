package com.xsslab.hrportal.model;

import java.sql.Timestamp;

public class CalendarEvent {
    private int id;
    private String title;
    private String description;
    private String eventDate;
    private String organizer;
    private Timestamp createdAt;

    public CalendarEvent() {}

    public CalendarEvent(String title, String description, String eventDate, String organizer) {
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.organizer = organizer;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
