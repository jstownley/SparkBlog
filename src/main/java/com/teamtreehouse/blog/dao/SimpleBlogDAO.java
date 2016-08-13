package com.teamtreehouse.blog.dao;

import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import com.teamtreehouse.blog.model.NotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathanTownley on 8/6/16.
 */
public class SimpleBlogDAO implements BlogDao {
    private List<BlogEntry> entries;

    public SimpleBlogDAO() {
        entries = new ArrayList<>();

        // And just so we don't have to keep writing blog entries every time
        // we run this code, let's seed the "server" with previously created
        // blog entries.
        String blogContent = "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, " +
            "vel tincidunt neque. Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien. Vivamus " +
            "pharetra massa libero, sed feugiat turpis efficitur at.</p>\n" +
            "<p>Cras egestas ac ipsum in posuere. Fusce suscipit, libero id malesuada placerat, orci velit semper " +
            "metus, quis pulvinar sem nunc vel augue. In ornare tempor metus, sit amet congue justo porta et. Etiam " +
            "pretium, sapien non fermentum consequat, <a href=\"\">dolor augue</a> gravida lacus, non accumsan " +
            "lorem odio id risus. Vestibulum pharetra tempor molestie. Integer sollicitudin ante ipsum, a luctus " +
            "nisi egestas eu. Cras accumsan cursus ante, non dapibus tempor.</p>";

        String commentContent = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, " +
            "vel tincidunt neque. Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien. Vivamus " +
            "pharetra massa libero, sed feugiat turpis efficitur at.";

        BlogEntry entry1 = new BlogEntry("The best day I’ve ever had", blogContent);
        entry1.addComment(new Comment("Carling Kirk", commentContent));
        entry1.addTag("Tagus 1us");
        addEntry(entry1);

        BlogEntry entry2 = new BlogEntry("The absolute worst day I’ve ever had", blogContent);
        entry2.addComment(new Comment("Carling Kirk", commentContent));
        addEntry(entry2);

        BlogEntry entry3 = new BlogEntry("That time at the mall", blogContent);
        entry3.addComment(new Comment("Carling Kirk", commentContent));
        entry3.addTag("Tagus 1us");
        entry3.addTag("Tagus 2us");
        addEntry(entry3);

        BlogEntry entry4 = new BlogEntry("Dude, where’s my car?", blogContent);
        entry4.addComment(new Comment("Carling Kirk", commentContent));
        addEntry(entry4);

    }


    @Override
    public boolean addEntry(BlogEntry entry) {
        return entries.add(entry);
    }

    @Override
    public boolean deleteEntry(BlogEntry entry) {
        return entries.remove(entry);
    }

    @Override
    public List<BlogEntry> findAllEntries() {
        return new ArrayList<>(entries);
    }

    @Override
    public BlogEntry findEntryBySlug(String slug) {
        return entries.stream()
            .filter(entry -> entry.getSlug().equals(slug))
            .findFirst()
            .orElseThrow(NotFoundException::new);
    }

    @Override
    public Comment findCommentEntryBySlug(String blogSlug, String commentSlug) {
        BlogEntry entry = findEntryBySlug(blogSlug);
        List<Comment> comments = entry.getComments();

        return comments.stream()
            .filter(comment -> comment.getSlug().equals(commentSlug))
            .findFirst()
            .orElseThrow(NotFoundException::new);
    }
}
