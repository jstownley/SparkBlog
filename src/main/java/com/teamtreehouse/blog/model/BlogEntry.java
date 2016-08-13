package com.teamtreehouse.blog.model;

import com.github.slugify.Slugify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlogEntry {
    private String title;
    private String content;
    private String date;
    private String slug;
    private List<Comment> comments;
    private List<String> tags;

    public BlogEntry(String title, String content) {
        this.title = title;
        this.content = content;
        this.date = new Date().toString();
        this.comments = new ArrayList<>();
        this.tags = new ArrayList<>();
        try {
            Slugify slugify = new Slugify();
            this.slug = slugify.slugify(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public String getSlug() {
        return slug;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public boolean addComment(Comment comment) {
        return this.comments.add(comment);
    }

    public boolean addTag(String tag) {
        return this.tags.add(tag);
    }

    public boolean removeTag(String tag) {
        return this.tags.remove(tag);
    }

    public List<String> getTags() {
        return this.tags;
    }

    public boolean removeTags() {
        return this.tags.removeAll(this.tags);
    }
}
