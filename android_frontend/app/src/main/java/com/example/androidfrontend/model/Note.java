package com.example.androidfrontend.model;

import java.util.Date;

/**
 * Note entity for SQLite database and UI layer.
 */
public class Note {
    private long id;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;

    public Note(long id, String title, String content, Date createdAt, Date updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Note(String title, String content, Date createdAt, Date updatedAt) {
        this(-1, title, content, createdAt, updatedAt);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
