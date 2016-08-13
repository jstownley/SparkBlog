package com.teamtreehouse.blog.dao;

import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;

import java.util.List;

public interface BlogDao {
    boolean addEntry(BlogEntry blogEntry);
    List<BlogEntry> findAllEntries();
    BlogEntry findEntryBySlug(String slug);
    Comment findCommentEntryBySlug(String blogSlug, String commentSlug);
    boolean deleteEntry(BlogEntry blogEntry);
}
