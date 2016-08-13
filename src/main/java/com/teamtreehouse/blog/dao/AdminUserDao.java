package com.teamtreehouse.blog.dao;

/**
 * Created by jonathanTownley on 8/10/16.
 */
public interface AdminUserDao {
    void addUser(String username, String password);
    boolean authenticateAdminUser(String username, String password);
}
