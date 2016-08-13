package com.teamtreehouse.blog.model;

import com.github.slugify.Slugify;

import java.io.IOException;
import java.util.Date;

public class Comment {
    private String author;
    private String content;
    private String date;
    private String slug;

    public Comment(String author, String content) {
        this.author = author;
        this.content = content;
        this.date = new Date().toString();
        try {
            Slugify slugify = new Slugify();
            this.slug = slugify.slugify(author + "-" + date);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getSlug() {
        return slug;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
