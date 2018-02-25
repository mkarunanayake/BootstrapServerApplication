package com.bootstrapserver.repository;

import com.bootstrapserver.model.User;
import com.bootstrapserver.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class UserRepository {
    DBConnection dbConn;

    public UserRepository() {this.dbConn = new DBConnection();}

    public User getUser(String uname){
        User user=null;
        Connection conn = dbConn.getConnection();
        String statement = "SELECT * FROM user_details WHERE username = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(statement);
            stmt.setString(1, uname);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                user = new User(rs.getInt("user_id") ,rs.getString("username"), rs.getString("password"),
                        rs.getInt("access_level"));
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void saveUser(User user){
        Connection conn = dbConn.getConnection();
        String userDetailsStmt = "INSERT INTO user_details (user_id, username, password) VALUES (?, ?, ?)";
        try {
            PreparedStatement userStmt = conn.prepareStatement(userDetailsStmt);
            userStmt.setInt(1, user.getUserID());
            userStmt.setString(2, user.getUsername());
            userStmt.setString(3, user.getPassword());
            userStmt.execute();
            userStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<User> getUsers(){
        ArrayList<User> users =new ArrayList<>();
        Connection connection = dbConn.getConnection();
        String getUsersStmt = "SELECT * FROM user_details";
        try {
            Statement stmt = connection.createStatement();
            ResultSet usersSet = stmt.executeQuery(getUsersStmt);
            while (usersSet.next()){
                users.add(new User(usersSet.getInt("user_id"), usersSet.getString("username"), usersSet.getString("password"), usersSet.getInt("access_level")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void updateUser(User user){
        Connection connection = dbConn.getConnection();
        String updateUser = "UPDATE user_details SET access_level=?, password = ? WHERE username=?";
        try {
            PreparedStatement preparedUpdateUserStmt= connection.prepareStatement(updateUser);
            preparedUpdateUserStmt.setInt(1, user.getAccessLevel());
            preparedUpdateUserStmt.setString(2, user.getPassword());
            preparedUpdateUserStmt.setString(3, user.getUsername());
            preparedUpdateUserStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setupUserTable(){
        Connection conn = dbConn.getConnection();
        String userTableStmt = "CREATE TABLE user_details(" +
                "user_id INT," +
                "username VARCHAR(20)," +
                "password VARCHAR (40) NOT NULL," +
                "access_level INT DEFAULT 2," +
                "CONSTRAINT check_access_level CHECK (access_level IN (1,2))," +
                "PRIMARY KEY (user_id))";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(userTableStmt);
            System.out.println("UserTable Created!");
        } catch (SQLException e) {
            if (e.getSQLState().equals("X0Y32")) {
                System.out.println("User Table Already Created!");
                return;
            }
            e.printStackTrace();
        }
    }
}
