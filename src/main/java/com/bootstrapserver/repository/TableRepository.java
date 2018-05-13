package com.bootstrapserver.repository;

import com.bootstrapserver.util.DBConnection;
import com.bootstrapserver.util.PasswordEncrypter;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TableRepository {
    public static void createTables() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Connection dbConnection = DBConnection.getDbConnection().getConnection();
        String peerTable = "CREATE TABLE peer_details(" +
                "user_id INT, " +
                "peer_address VARCHAR(15)," +
                "peer_port INT," +
                "last_seen BIGINT," +
                "PRIMARY KEY (user_id)," +
                "FOREIGN KEY (user_id) references user_details)";
        String userTable = "CREATE TABLE user_details(" +
                "user_id INT," +
                "username VARCHAR(20)," +
                "password VARCHAR (40) NOT NULL," +
                "access_level INT DEFAULT 2," +
                "CONSTRAINT check_access_level CHECK (access_level IN (1,2))," +
                "PRIMARY KEY (user_id))";
        String systemUserTable = "CREATE TABLE sys_user(" +
                "username VARCHAR (20) PRIMARY KEY," +
                "password VARCHAR (40))";
        String insertUser = "INSERT INTO sys_user(username, password) VALUES (?, ?)";
        try {
            Statement statement = dbConnection.createStatement();
            statement.execute(systemUserTable);
            statement.execute(userTable);
            statement.execute(peerTable);
            PreparedStatement preparedStatement = dbConnection.prepareStatement(insertUser);
            preparedStatement.setString(1, "admin");
            preparedStatement.setString(2, PasswordEncrypter.SHA1("Admin"));
            preparedStatement.execute();
        } catch (SQLException e) {
            if (e.getSQLState().equals("X0Y32")) {
                System.out.println("User Table Already Created!");
                return;
            }
            e.printStackTrace();
        }

    }
}
