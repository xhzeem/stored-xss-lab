package com.xsslab.hrportal.model;

import java.sql.Timestamp;

public class Announcement {
    private int id;
    private String author;
    private String title;
    private String content;
    private Timestamp createdAt;

    public Announcement() {}

    public Announcement(String author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
