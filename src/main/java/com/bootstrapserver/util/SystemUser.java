package com.bootstrapserver.util;

public class SystemUser {

    private static String username;
    private static String password;


    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        SystemUser.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        SystemUser.password = password;
    }
}
