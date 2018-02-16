package com.bootstrapserver.repository;

import com.bootstrapserver.model.User;
import com.bootstrapserver.util.DBConnection;

import java.sql.*;

public class UserRepository {
    DBConnection dbConn;

    public UserRepository() {this.dbConn = new DBConnection();}

    public User getUser(String username){
        User user=null;
        Connection conn = dbConn.getConnection();
        String statement = "SELECT * FROM user_details WHERE username="+username;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(statement);
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

    public void setupUserTable(){
        Connection conn = dbConn.getConnection();
        String userTableStmt = "CREATE TABLE user_details(" +
                "user_id INT," +
                "username VARCHAR(20)," +
                "password VARCHAR (40) NOT NULL," +
                "access_level DEFAULT 2" +
                "CONSTRAINT check_access_level CHECK (access_level IN (1,2)))" +
                "PRIMARY KEY (user_id))";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(userTableStmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
