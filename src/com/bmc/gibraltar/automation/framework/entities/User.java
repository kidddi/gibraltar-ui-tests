package com.bmc.gibraltar.automation.framework.entities;

/**
 * Created by energetic on 17.06.2016.
 */
public class User {
    private String Username = "root";
    private String Password = "root";

    public User(String password, String username) {
        Password = password;
        Username = username;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }
}
