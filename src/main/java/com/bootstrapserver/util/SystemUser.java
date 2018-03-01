package com.bootstrapserver.util;

public class SystemUser {

    private static String username = "admin";
    private static String password = "4e7afebcfbae000b22c7c85e5560f89a2a0280b4";


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
