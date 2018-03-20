package com.bootstrapserver.model;

public class User {
    private String username;
    private String password;
    private int accessLevel;
    private int userID;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(int userID, String username, String password, int accessLevel) {
        this.username = username;
        this.password = password;
        this.accessLevel = accessLevel;
        this.userID = userID;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
