package com.teamtreehouse.blog.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonathanTownley on 8/10/16.
 */
public class SimpleAdminUserDAO implements AdminUserDao{
    private Map<String, String> adminUsers = new HashMap<>();

    public SimpleAdminUserDAO() {
        /* Now, let's pretend that our blog user has already set up an admin username
           and password on the real server where they're hosting their blog.  This way,
           we can keep the username and password completely segregated from the blog code.*/
        adminUsers.put("admin","password");
    }

    @Override
    public void addUser(String username, String password) {
        adminUsers.put(username,password);
    }

    @Override
    public boolean authenticateAdminUser(String username, String password) {
        if (adminUsers.isEmpty()) {
            return false;
        }
        if (null == adminUsers.get(username)) {
            return false;
        }
        return (0 == adminUsers.get(username).compareTo(password));
    }
}
