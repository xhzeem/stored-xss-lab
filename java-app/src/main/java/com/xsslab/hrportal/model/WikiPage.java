package com.xsslab.hrportal.model;

import java.sql.Timestamp;

public class WikiPage {
    private int id;
    private String title;
    private String author;
    private String content;
    private Timestamp createdAt;

    public WikiPage() {}

    public WikiPage(String title, String author, String content) {
        this.title = title;
        this.author = author;
        this.content = content;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
