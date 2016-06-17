package com.bmc.gibraltar.automation.framework.utils;

/**
 * Created by energetic on 17.06.2016.
 */
public class PropertiesUtils {
    private static String Username = "root";
    private static String Password = "root";
    private static String AppServerUrl = "www.google.com";


    public static String getUsername() {
        return Username;
    }

    public static String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public static String getAppServerUrl() {
        return AppServerUrl;
    }

    public void setUserName(String user) {
        Username = user;
    }
}
