package com.bootstrapserver.model;

public class User {
    private String username;
    private String password;
    private int access_level;
    private int userID;

    public User(){    }

    public User(String username){ this.username = username;}

    public User(int userID, String username, String password, int access_level){
        this.username=username;
        this.password=password;
        this.access_level=access_level;
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

    public int getAccess_level() {
        return access_level;
    }

    public void setAccess_level(int access_level) {
        this.access_level = access_level;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
