/**
 * Created by jonathanTownley on 8/5/16.
 */

import com.github.jknack.handlebars.Handlebars;
import com.teamtreehouse.blog.dao.SimpleAdminUserDAO;
import com.teamtreehouse.blog.dao.SimpleBlogDAO;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import com.teamtreehouse.blog.model.NotFoundException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;
import java.util.*;

import static spark.Spark.*;


public class Main {

    private static final String FLASH_MESSAGE_KEY = "flash_message";

    public static void main(String[] args) {
        staticFileLocation("/public");

        SimpleBlogDAO blogDao = new SimpleBlogDAO();
        SimpleAdminUserDAO adminUserDao = new SimpleAdminUserDAO();

        before((req, res) -> {
            if (null != req.cookie("username")) {
                req.attribute("username", req.cookie("username"));
            }
            if (null != req.cookie("previous")) {
                req.attribute("previous", req.cookie("previous"));
            }
        });

        before("/new", (req, res) -> {
            if (req.attribute("username") == null) {
                setFlashMessage(req, "You must sign in before creating a new post");
                res.cookie("/password", "previous", "/new", -1, false);
                res.redirect("/password");
                halt();
            }
        });

        before("/:slug/edit", (req, res) -> {
            if (req.attribute("username") == null) {
                setFlashMessage(req, "You must sign in before editing a post");
                // For some reason, spark doesn't like doing cookies with named parameters
                // unless you overload the cookie() method with the desired path name.  Otherwise,
                // the path name is set to be the page from which you set the cookie, which is
                // /{{ slug }} in this case.
                res.cookie("/password", "previous", "/" + req.params(":slug") + "/edit", -1, false);
                res.redirect("/password");
                halt();
            }
        });

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries",blogDao.findAllEntries());
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "/index.hbs");
        }, new HandlebarsTemplateEngine());

        get("/password", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "/password.hbs");
        }, new HandlebarsTemplateEngine());

        post("/password", (req, res) -> {
            if (adminUserDao.authenticateAdminUser(req.queryParams("username"),
                    req.queryParams("password"))) {
                res.cookie("username", req.queryParams("username"));
                if (req.attributes().contains("previous")) {
                    if (null != req.attribute("previous")) {
                        res.redirect(req.attribute("previous"));
                        halt();
                    }
                }
                else {
                    res.redirect("/");
                    halt();
                }
            }
            else {
                setFlashMessage(req, "Invalid username/password");
                res.redirect("/password");
                halt();
            }
            return null;
        });

        get("/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "new.hbs");
        }, new HandlebarsTemplateEngine());

        post("/new", (req, res) -> {
            BlogEntry entry = new BlogEntry(req.queryParams("title"),req.queryParams("entry"));
            blogDao.addEntry(entry);
            res.redirect("/" + entry.getSlug());
            return null;
        });

        get("/:slug", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            BlogEntry entry = blogDao.findEntryBySlug(req.params(":slug"));
            model.put("entry", entry);
            model.put("comments", entry.getComments());
            model.put("flashMessage", captureFlashMessage(req));
            String username = req.attribute("username");
            if (null != username) {
                if (0 == username.compareTo("admin")) {
                    model.put("username", username);
                }
            }
            return new ModelAndView(model,"/detail.hbs");
        }, new HandlebarsTemplateEngine());

        post("/:slug", (req, res) -> {
            BlogEntry entry = blogDao.findEntryBySlug(req.params(":slug"));
            if ( (null != req.queryParams("name")) && (null != req.queryParams("comment")) ) {
                entry.addComment(new Comment(req.queryParams("name"),req.queryParams("comment")));
                setFlashMessage(req, "Comment added");
            }
            res.redirect("/" + req.params(":slug"));
            return null;
        });

        get("/:slug/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            BlogEntry entry = blogDao.findEntryBySlug(req.params(":slug"));
            // Convert the list of tags into a CSV string for easier editing
            List<String> tags = entry.getTags();
            String tagListString = "";
            for (int ii=0; ii<tags.size(); ii++) {
                if (ii < tags.size()-1) {
                    tagListString += tags.get(ii) + ", ";
                }
                else {
                    // We don't want to end the list with ", "
                    tagListString += tags.get(ii);
                }
            }
            model.put("entry", entry);
            model.put("comments", entry.getComments());
            model.put("tagListString", tagListString);
            return new ModelAndView(model,"/edit.hbs");
        }, new HandlebarsTemplateEngine());

        post("/:slug/edit", (req, res) -> {
            BlogEntry entry = blogDao.findEntryBySlug(req.params(":slug"));
            if ( (null!= req.queryParams("title")) && (null != req.queryParams("entry")) ) {
                entry.setTitle(req.queryParams("title"));
                entry.setContent(req.queryParams("entry"));
                setFlashMessage(req, "Post updated");
            }
            if (null != req.queryParams("admin-edit-comment")) {
                Comment comment = blogDao.findCommentEntryBySlug(req.params(":slug"), req.queryParams("comment-slug"));
                comment.setContent(req.queryParams("admin-edit-comment"));
                setFlashMessage(req, "Comment updated");
            }
            if (null != req.queryParams("delete")) {
                blogDao.deleteEntry(blogDao.findEntryBySlug(req.params(":slug")));
                setFlashMessage(req, "Post deleted");
                res.redirect("/");
                halt();
            }
            if (null != req.queryParams("tagListString")) {
                // Convert the CSV string of tags to a list
                String tagListString = req.queryParams("tagListString");
                String[] tagListArray = tagListString.split(",");
                entry.removeTags();
                for (String tag : tagListArray) {
                    entry.addTag(tag.trim());
                }
                setFlashMessage(req, "Tag list updated");
            }
            res.redirect("/" + req.params(":slug"));
            return null;
        });

        get("/tag/:tag", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<BlogEntry> entries = blogDao.findAllEntries();
            List<BlogEntry> taggedEntries = new ArrayList<>();
            for (BlogEntry entry : entries) {
                List<String> theseTags = entry.getTags();
                if (theseTags.contains((String) req.params("tag"))) {
                    taggedEntries.add(entry);
                }
            }
            model.put("entries", taggedEntries);
            model.put("tag", req.params(":tag"));
            return new ModelAndView(model, "tagged-entries.hbs");
        }, new HandlebarsTemplateEngine());

        exception(NotFoundException.class, (exc, req, res) -> {
            res.status(404);
            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
            String html = engine.render(new ModelAndView(null, "not-found.hbs"));
            res.body(html);
        });
    }

    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    private static String getFlashMessage(Request req) {
        if (null == req.session(false)) {
            return null;
        }

        if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)) {
            return null;
        }

        return (String) req.session().attribute(FLASH_MESSAGE_KEY);
    }

    private static String captureFlashMessage(Request req) {
        String message = getFlashMessage(req);
        if (message != null) {
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }
}
